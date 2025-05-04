package net.ins.prototype.backend.profile.converter

fun interface Converter<IN, OUT> {
    fun convert(source: IN): OUT
}