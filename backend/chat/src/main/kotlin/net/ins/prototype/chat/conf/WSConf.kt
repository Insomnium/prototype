package net.ins.prototype.chat.conf

import net.ins.prototype.chat.auth.interceptor.UserIdAuthChannelInterceptor
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.simp.config.ChannelRegistration
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import org.springframework.web.socket.config.annotation.StompEndpointRegistry
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer

@Configuration
@EnableWebSocketMessageBroker
class WSConf(
    private val userIdAuthChannelInterceptor: UserIdAuthChannelInterceptor,
) : WebSocketMessageBrokerConfigurer {

    override fun configureMessageBroker(registry: MessageBrokerRegistry): Unit = with(registry) {
        enableSimpleBroker("/topic")
        setApplicationDestinationPrefixes("/app")
    }

    override fun registerStompEndpoints(registry: StompEndpointRegistry): Unit = with(registry) {
        addEndpoint("/ws")
            .setAllowedOrigins("http://localhost:63342")
//            .addInterceptors(HttpSessionHandshakeInterceptor().apply {
//                isCreateSession = true
//            })
//            .setHandshakeHandler(handshakeInterceptor)
            .withSockJS()
    }


    override fun configureClientInboundChannel(registration: ChannelRegistration) {
        registration.interceptors(userIdAuthChannelInterceptor)
    }
}
