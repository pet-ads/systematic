package br.all.report.presenter

import org.springframework.core.io.ByteArrayResource
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity


import java.util.UUID

fun buildDownloadResponse(
    format: String,
    systematicStudyId: UUID,
    prefix: String,
    content: String
): ResponseEntity<*> {
    val fileExtension = when (format.lowercase()) {
        "latex" -> "tex"
        "csv" -> "csv"
        "json" -> "json"
        "pdf" -> "pdf"
        else -> "txt"
    }
    val mediaType = when (format.lowercase()) {
        "latex" -> "application/x-latex"
        "csv" -> "text/csv"
        "json" -> "application/json"
        "pdf" -> "application/pdf"
        else -> "text/plain"
    }
    val resource = ByteArrayResource(content.toByteArray(Charsets.UTF_8))
    val headers = HttpHeaders().apply {
        add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"${prefix}_$systematicStudyId.$fileExtension\"")
        contentType = MediaType.parseMediaType(mediaType)
    }
    return ResponseEntity.status(HttpStatus.OK).headers(headers).body(resource)
}

