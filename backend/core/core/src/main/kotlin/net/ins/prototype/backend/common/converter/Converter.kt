package net.ins.prototype.backend.common.converter

fun interface Converter<IN, OUT> {
    fun convert(source: IN): OUT
}
