package net.ins.prototype.chat.auth.model

import java.security.Principal

class UnauthorizedPrincipal : Principal {

    override fun getName(): String = "Unauthorized"
}
