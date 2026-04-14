package net.ins.prototype.chat.socket.auth.interceptor

import net.ins.prototype.chat.socket.auth.P2pWsQueryParams
import net.ins.prototype.chat.socket.auth.P2pWsSessionAttributes
import org.springframework.http.server.ServerHttpRequest
import org.springframework.http.server.ServerHttpResponse
import org.springframework.stereotype.Component
import org.springframework.web.socket.WebSocketHandler
import org.springframework.web.socket.server.HandshakeInterceptor
import org.springframework.web.util.UriComponentsBuilder
import java.lang.Exception

@Component
class HandshakeQueryParamSenderIdExtractorInterceptor : HandshakeInterceptor {
    override fun beforeHandshake(
        request: ServerHttpRequest,
        response: ServerHttpResponse,
        wsHandler: WebSocketHandler,
        attributes: MutableMap<String?, Any?>
    ): Boolean {
        val userId = UriComponentsBuilder.fromUri(request.uri)
            .build()
            .queryParams
            .getFirst(P2pWsQueryParams.USER_ID)
        attributes[P2pWsSessionAttributes.USER_ID] = userId
        return true
    }

    override fun afterHandshake(
        request: ServerHttpRequest,
        response: ServerHttpResponse,
        wsHandler: WebSocketHandler,
        exception: Exception?
    ) {
        // no-op
    }
}
