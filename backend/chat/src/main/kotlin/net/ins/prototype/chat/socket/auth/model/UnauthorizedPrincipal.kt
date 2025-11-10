package net.ins.prototype.chat.socket.auth.model

import java.security.Principal

class UnauthorizedPrincipal : Principal {

    override fun getName(): String = "Unauthorized"
}
