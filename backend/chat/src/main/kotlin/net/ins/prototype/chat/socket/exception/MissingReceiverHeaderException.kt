package net.ins.prototype.chat.socket.exception

import net.ins.prototype.chat.socket.auth.P2pWsHeaders

class MissingReceiverHeaderException : MissingMandatoryHeaderException(P2pWsHeaders.RECEIVER)
