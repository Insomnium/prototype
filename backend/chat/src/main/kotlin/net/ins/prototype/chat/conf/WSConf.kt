package net.ins.prototype.chat.conf

import org.springframework.context.annotation.Configuration
import org.springframework.messaging.converter.MessageConverter
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import org.springframework.web.socket.config.annotation.StompEndpointRegistry
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer

@Configuration
@EnableWebSocketMessageBroker
class WSConf : WebSocketMessageBrokerConfigurer {

    override fun configureMessageBroker(registry: MessageBrokerRegistry): Unit = with(registry) {
        enableSimpleBroker("/topic")
        setApplicationDestinationPrefixes("/app")
    }

    override fun registerStompEndpoints(registry: StompEndpointRegistry): Unit = with(registry) {
        addEndpoint("/ws")
            .setAllowedOrigins("http://localhost:63342")
            .withSockJS()
    }

    override fun configureMessageConverters(messageConverters: List<MessageConverter?>): Boolean {
        return super.configureMessageConverters(messageConverters)
    }
}
