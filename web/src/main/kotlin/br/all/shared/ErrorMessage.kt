package br.all.shared

import org.springframework.http.HttpStatus
import java.time.Instant
import java.time.format.DateTimeFormatter

class ErrorMessage (status: HttpStatus, exceptionMessage: String?){
    val code = status.value()
    val message = status.reasonPhrase
    val detail = exceptionMessage
    val timestamp = generateTimestamp()

    private fun generateTimestamp(): String{
        val timestamp = Instant.now()
        val formatter = DateTimeFormatter.ISO_INSTANT
        return formatter.format(timestamp)
    }
}