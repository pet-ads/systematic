package br.all.shared.error

import br.all.shared.util.generateTimestamp
import org.springframework.http.HttpStatus

class ErrorMessage (status: HttpStatus, throwable: Throwable){
    val code = status.value()
    val message = status.reasonPhrase
    val detail = throwable.message
    val timestamp = generateTimestamp()
    val developerMessage = throwable.javaClass
}