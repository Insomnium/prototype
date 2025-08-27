package net.ins.prototype.chat.auth.exception

import net.ins.prototype.chat.auth.P2pHeaders

class MissingSenderHeaderException : MissingMandatoryHeaderException(P2pHeaders.SENDER)
