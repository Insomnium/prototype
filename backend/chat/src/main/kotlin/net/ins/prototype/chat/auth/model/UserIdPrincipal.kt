package net.ins.prototype.chat.auth.model

import java.security.Principal

data class UserIdPrincipal(val id: String) : Principal {

    override fun getName(): String = id
}
