package br.all.shared.error

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler


@ControllerAdvice
class ApiExceptionHandler {

    @ExceptionHandler(value = [RuntimeException::class])
    fun handleRuntimeException(exception: RuntimeException): ResponseEntity<*> {
        return createErrorResponseFrom(throwable = exception)
    }
}