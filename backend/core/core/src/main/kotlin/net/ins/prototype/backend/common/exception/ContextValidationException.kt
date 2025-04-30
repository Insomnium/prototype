package net.ins.prototype.backend.common.exception

class ContextValidationException(
    val code: String,
    message: String,
) : RuntimeException(message)
