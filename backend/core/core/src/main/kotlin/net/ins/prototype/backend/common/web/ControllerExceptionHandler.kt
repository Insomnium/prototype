package net.ins.prototype.backend.common.web

import net.ins.prototype.backend.common.web.model.FieldValidationError
import net.ins.prototype.backend.common.web.model.RequestValidationResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class ControllerExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValid(e: MethodArgumentNotValidException): ResponseEntity<RequestValidationResponse> {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(RequestValidationResponse(e.bindingResult.fieldErrors.map { FieldValidationError(it.field, it.defaultMessage ?: "Invalid value") }))
    }
}
