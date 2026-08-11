package br.all.report.presenter

import br.all.application.report.export.presenter.ExportReviewPresenter
import br.all.application.report.export.service.ExportReviewService
import br.all.shared.error.createErrorResponseFrom
import org.springframework.http.ResponseEntity

class DownloadableReviewPresenter : ExportReviewPresenter {

    var responseEntity: ResponseEntity<*>? = null

    override fun prepareSuccessView(response: ExportReviewService.ResponseModel) {
        responseEntity = buildDownloadResponse(
            response.format,
            response.systematicStudyId,
            "review",
            response.formattedReview
        )
    }

    override fun prepareFailView(throwable: Throwable) {
        responseEntity = createErrorResponseFrom(throwable)
    }

    override fun isDone() = responseEntity != null
}

