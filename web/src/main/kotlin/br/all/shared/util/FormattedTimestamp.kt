package br.all.shared.util

import java.time.Instant
import java.time.format.DateTimeFormatter

fun generateTimestamp(): String{
    val timestamp = Instant.now()
    val formatter = DateTimeFormatter.ISO_INSTANT
    return formatter.format(timestamp)
}