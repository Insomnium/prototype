package net.ins.prototype.chat.auth.exception

open class MissingMandatoryHeaderException(key: String) : RuntimeException("Missing $key header")
