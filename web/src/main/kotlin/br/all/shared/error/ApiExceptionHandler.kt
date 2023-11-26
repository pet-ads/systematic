package br.all.shared.error

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler


@ControllerAdvice
class ApiExceptionHandler {

    @ExceptionHandler(value = [IllegalArgumentException::class])
    fun handleIllegalArgumentException(e: IllegalArgumentException): ResponseEntity<*> {
        val status = HttpStatus.BAD_REQUEST
        val apiException = ErrorMessage(status, e)
        return ResponseEntity<Any>(apiException, status)
    }
}