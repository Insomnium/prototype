package net.ins.prototype.backend.common.web.model

data class RequestValidationResponse(
    val errors: List<FieldValidationError>,
)
