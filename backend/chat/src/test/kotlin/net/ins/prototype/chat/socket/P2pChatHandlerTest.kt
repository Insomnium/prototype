package net.ins.prototype.chat.socket

import io.confluent.kafka.serializers.protobuf.KafkaProtobufDeserializer
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.equals.shouldBeEqual
import net.ins.prototype.chat.AbstractTestcontainersTest
import net.ins.prototype.chat.PrototypeStompClientSessionProvider
import net.ins.prototype.chat.event.P2pKafkaHeaders
import net.ins.prototype.chat.event.P2pMessageEvent
import net.ins.prototype.chat.model.ChatMessageRequest
import net.ins.prototype.chat.socket.auth.P2pConstants
import org.apache.kafka.common.serialization.StringDeserializer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles

@Import(AbstractTestcontainersTest::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class P2pChatHandlerTest : AbstractTestcontainersTest() {

    companion object {
        private const val USER_A = 1
        private const val USER_B = 2
        private const val USER_C = 3

        private const val MESSAGE_AWAIT_TIMEOUT_MILLIS = 10000L
    }

    @LocalServerPort
    private var serverPort: Int = 0

    private val sessionProvider: PrototypeStompClientSessionProvider = PrototypeStompClientSessionProvider()

    @AfterEach
    fun tearDown() {
        sessionProvider.terminate()
    }

    @Test
    fun `should establish session and receive admin hello`() {
        val sessionWrapperA = sessionProvider.establishSession(
            serverPort = serverPort,
            userId = USER_A,
        )

        val receivedMessagesA = sessionWrapperA.awaitForMessages(
            expectedMessagesCount = 1,
            userMessagesOnly = false,
            awaitMs = MESSAGE_AWAIT_TIMEOUT_MILLIS,
        )

        receivedMessagesA shouldHaveSize 1
        receivedMessagesA[0].sender shouldBeEqual P2pConstants.ADMIN_SENDER_ID
        receivedMessagesA[0].content shouldBeEqual "Welcome back online"
    }

    @Test
    fun `should exchange messages`() {
        // given 3 user sessions established:
        val sessionWrapperA = sessionProvider.establishSession(serverPort, userId = USER_A)
        val sessionWrapperB = sessionProvider.establishSession(serverPort, userId = USER_B)
        val sessionWrapperC = sessionProvider.establishSession(serverPort, userId = USER_C)

        // when: USER_A sends message to USER_B
        val messageContent = "Hey, UserB"
        sessionWrapperA.sendMessage(receiverId = USER_B, payload = ChatMessageRequest(messageContent))

        // then: USER_B receives message
        val receivedMessagesB = sessionWrapperB.awaitForMessages(expectedMessagesCount = 1, awaitMs = MESSAGE_AWAIT_TIMEOUT_MILLIS)
        receivedMessagesB shouldHaveSize 1
        receivedMessagesB[0].sender shouldBeEqual USER_A.toString()
        receivedMessagesB[0].content shouldBeEqual messageContent

        // and: USER_A does not receive any messages
        val receivedMessagesA = sessionWrapperA.awaitForMessages(expectedMessagesCount = Int.MAX_VALUE, awaitMs = MESSAGE_AWAIT_TIMEOUT_MILLIS)
        receivedMessagesA.shouldBeEmpty()

        // and: USER_C does not receive any messages
        val receivedMessagesC = sessionWrapperC.awaitForMessages(expectedMessagesCount = Int.MAX_VALUE, awaitMs = MESSAGE_AWAIT_TIMEOUT_MILLIS)
        receivedMessagesC.shouldBeEmpty()

        assertEventPublished<String, P2pMessageEvent>(
            topic = appProperties.integrations.topics.p2pMessage.name,
            keyDeserializer = StringDeserializer(),
            valueDeserializer = KafkaProtobufDeserializer(
                schemaRegistryClient,
                appProperties.kafka.consumer.buildProperties(null),
                P2pMessageEvent::class.java,
            ),
            expectedRecordsCount = 1,
        ) {
            assertSoftly {
                it shouldHaveSize 1
                val p2pMessageEvent = it.first()

                val chatRoomHeaders = p2pMessageEvent.headers().headers(P2pKafkaHeaders.CHAT_ROOM)
                chatRoomHeaders shouldHaveSize 1
                chatRoomHeaders.first().value().toString(charset = Charsets.UTF_8) shouldBeEqual "p2p_${USER_A}_${USER_B}"

                val senderHeaders = p2pMessageEvent.headers().headers(P2pKafkaHeaders.SENDER)
                senderHeaders shouldHaveSize 1
                senderHeaders.first().value().toString(charset = Charsets.UTF_8) shouldBeEqual USER_A.toString()

                val receiverHeaders = p2pMessageEvent.headers().headers(P2pKafkaHeaders.RECEIVER)
                receiverHeaders shouldHaveSize 1
                receiverHeaders.first().value().toString(charset = Charsets.UTF_8) shouldBeEqual USER_B.toString()

                val body = p2pMessageEvent.value()
                body.content shouldBeEqual messageContent
                body.senderId shouldBeEqual USER_A.toString()
                body.receiverId shouldBeEqual USER_B.toString()
            }
        }
    }
}
