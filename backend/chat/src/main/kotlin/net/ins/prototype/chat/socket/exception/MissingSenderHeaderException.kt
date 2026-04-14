package net.ins.prototype.chat.socket.exception

import net.ins.prototype.chat.socket.auth.P2pWsHeaders

class MissingSenderHeaderException : MissingMandatoryHeaderException(P2pWsHeaders.SENDER)
