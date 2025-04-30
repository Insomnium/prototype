package net.ins.prototype.backend.profile.service.validator

interface Validator<in T> {

    fun validate(source: T)
}
