package net.ins.prototype.chat.auth.exception

import net.ins.prototype.chat.auth.P2pHeaders

class MissingReceiverHeaderException : MissingMandatoryHeaderException(P2pHeaders.RECEIVER)
