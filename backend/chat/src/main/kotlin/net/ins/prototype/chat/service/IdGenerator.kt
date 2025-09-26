package net.ins.prototype.chat.service

interface IdGenerator<R, C> {

    fun generateId(context: C): R
}
