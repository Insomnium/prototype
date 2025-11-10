package net.ins.prototype.chat.socket.exception

open class MissingMandatoryHeaderException(key: String) : RuntimeException("Missing $key header")
