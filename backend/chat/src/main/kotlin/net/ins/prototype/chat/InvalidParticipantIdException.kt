package net.ins.prototype.chat

class InvalidParticipantIdException(id: String) : RuntimeException("Invalid participant id: must be long numeric value, but was: $id")
