package net.ins.prototype.chat.auth.exception

import net.ins.prototype.chat.auth.P2pWsHeaders

class MissingReceiverHeaderException : MissingMandatoryHeaderException(P2pWsHeaders.RECEIVER)
