package net.ins.prototype.chat.auth.exception

import net.ins.prototype.chat.auth.P2pWsHeaders

class MissingSenderHeaderException : MissingMandatoryHeaderException(P2pWsHeaders.SENDER)
