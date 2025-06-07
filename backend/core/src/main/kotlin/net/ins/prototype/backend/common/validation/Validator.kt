package net.ins.prototype.backend.common.validation

abstract class Validator<T> {

    protected abstract fun performValidation(source: T)

    fun validate(source: T): T {
        performValidation(source)
        return source
    }
}
