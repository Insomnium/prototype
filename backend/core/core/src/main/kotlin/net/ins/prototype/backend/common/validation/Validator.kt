package net.ins.prototype.backend.common.validation

interface Validator<in T> {

    fun validate(source: T)
}
