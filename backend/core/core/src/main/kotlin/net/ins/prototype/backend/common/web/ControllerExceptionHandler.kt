package net.ins.prototype.backend.common.web

import net.ins.prototype.backend.common.web.model.FieldValidationError
import net.ins.prototype.backend.common.web.model.RequestValidationResponse
import net.ins.prototype.backend.common.exception.ContextValidationException
import net.ins.prototype.backend.common.exception.EntityNotFoundException
import net.ins.prototype.backend.common.web.model.InvalidRequestResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class ControllerExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValid(e: MethodArgumentNotValidException): ResponseEntity<RequestValidationResponse> = ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(RequestValidationResponse(e.bindingResult.fieldErrors.map { FieldValidationError(it.field, it.defaultMessage ?: "Invalid value") }))

    @ExceptionHandler(ContextValidationException::class)
    fun handleContextValidationException(e: ContextValidationException): ResponseEntity<InvalidRequestResponse> = ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(InvalidRequestResponse(e.code, e.message ?: "Unknown error occurred"))

    @ExceptionHandler(EntityNotFoundException::class)
    fun handleEntityNotFoundException(e: EntityNotFoundException): ResponseEntity<InvalidRequestResponse> = ResponseEntity
        .status(HttpStatus.NOT_FOUND)
        .body(InvalidRequestResponse("entity.notFound", e.message ?: "No entity found"))
}
