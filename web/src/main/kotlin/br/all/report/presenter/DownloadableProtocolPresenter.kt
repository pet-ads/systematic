package br.all.report.presenter

import br.all.application.report.find.presenter.ExportProtocolPresenter
import br.all.application.report.find.service.ExportProtocolService
import br.all.shared.error.createErrorResponseFrom
import org.springframework.core.io.ByteArrayResource
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity

class DownloadableProtocolPresenter : ExportProtocolPresenter {

    var responseEntity: ResponseEntity<*>? = null

    override fun prepareSuccessView(response: ExportProtocolService.ResponseModel) {
        val fileExtension = when (response.format.lowercase()) {
            "latex" -> "tex"
            "csv" -> "csv"
            "json" -> "json"
            "pdf" -> "pdf"
            else -> "txt"
        }

        val mediaType = when (response.format.lowercase()) {
            "latex" -> "application/x-latex"
            "csv" -> "text/csv"
            "json" -> "application/json"
            "pdf" -> "application/pdf"
            else -> "text/plain"
        }

        val filename = "protocol_${response.systematicStudyId}.$fileExtension"

        val resource = ByteArrayResource(response.formattedProtocol.toByteArray(Charsets.UTF_8))

        val headers = HttpHeaders().apply {
            add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"$filename\"")
            contentType = MediaType.parseMediaType(mediaType)
        }

        responseEntity = ResponseEntity.status(HttpStatus.OK).headers(headers).body(resource)
    }

    override fun prepareFailView(throwable: Throwable) {
        responseEntity = createErrorResponseFrom(throwable)
    }

    override fun isDone() = responseEntity != null
}
