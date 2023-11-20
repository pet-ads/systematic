package br.all.shared

import br.all.application.shared.exceptions.EntityNotFoundException
import br.all.application.shared.exceptions.UnauthenticatedUserException
import br.all.application.shared.exceptions.UnauthorizedUserException
import br.all.application.shared.exceptions.UniquenessViolationException
import org.springframework.http.HttpStatus.*
import org.springframework.http.ResponseEntity

fun createErrorResponseFrom(throwable: Throwable): ResponseEntity<ErrorMessage> {
    val httpStatus = when (throwable) {
        is UnauthenticatedUserException -> UNAUTHORIZED
        is UnauthorizedUserException -> FORBIDDEN
        is EntityNotFoundException -> NOT_FOUND
        is UniquenessViolationException -> CONFLICT
        else -> BAD_REQUEST
    }
    val errorMessage = ErrorMessage(httpStatus, throwable.message)
    return ResponseEntity.status(httpStatus).body(errorMessage)
}