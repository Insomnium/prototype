package net.ins.prototype.chat.handler

import net.ins.prototype.chat.auth.model.UnauthorizedPrincipal
import net.ins.prototype.chat.auth.model.UserIdPrincipal
import org.springframework.http.server.ServerHttpRequest
import org.springframework.http.server.ServletServerHttpRequest
import org.springframework.stereotype.Component
import org.springframework.web.socket.WebSocketHandler
import org.springframework.web.socket.server.support.DefaultHandshakeHandler
import java.security.Principal

@Component
class UserSessionHandshakeInterceptor : DefaultHandshakeHandler() {

    override fun determineUser(
        request: ServerHttpRequest,
        wsHandler: WebSocketHandler,
        attributes: MutableMap<String, Any>,
    ): Principal = if (request is ServletServerHttpRequest)
        UserIdPrincipal(request.servletRequest.getHeader("X-sender-id"))
    else UnauthorizedPrincipal()
}
