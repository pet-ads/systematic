package br.all.shared.error

import br.all.domain.shared.exception.EntityNotFoundException
import br.all.domain.shared.exception.UnauthenticatedUserException
import br.all.domain.shared.exception.UnauthorizedUserException
import br.all.domain.shared.exception.UniquenessViolationException
import org.springframework.http.HttpStatus.*
import org.springframework.http.ResponseEntity

fun createErrorResponseFrom(throwable: Throwable): ResponseEntity<ErrorMessage> {
    val httpStatus = when (throwable) {
        is UnauthenticatedUserException -> UNAUTHORIZED
        is UnauthorizedUserException -> FORBIDDEN
        is EntityNotFoundException -> NOT_FOUND
        is NoSuchElementException -> NOT_FOUND
        is UniquenessViolationException -> CONFLICT
        else -> BAD_REQUEST
    }
    val errorMessage = ErrorMessage(httpStatus, throwable)
    return ResponseEntity.status(httpStatus).body(errorMessage)
}