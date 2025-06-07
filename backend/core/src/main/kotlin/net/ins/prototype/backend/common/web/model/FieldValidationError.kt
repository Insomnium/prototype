package net.ins.prototype.backend.common.web.model

data class FieldValidationError(
    val field: String,
    val message: String,
)
