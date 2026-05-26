package br.all.report.presenter

import br.all.application.report.export.presenter.ExportReviewPresenter
import br.all.application.report.export.service.ExportReviewService
import br.all.shared.error.createErrorResponseFrom
import br.all.utils.LinksFactory
import org.springframework.hateoas.RepresentationModel
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.util.UUID

class RestfulExportReviewPresenter(
    private val linkFactory: LinksFactory
) : ExportReviewPresenter {
    var responseEntity: ResponseEntity<*>? = null

    override fun prepareSuccessView(response: ExportReviewService.ResponseModel) {
        val restfulResponse = ViewModel(
            response.userId,
            response.systematicStudyId,
            response.formattedReview
        )
        val selfRef = linkFactory.exportReview(response.systematicStudyId,response.format, downloadable = true)
        restfulResponse.add(selfRef)
        responseEntity = ResponseEntity.status(HttpStatus.OK).body(restfulResponse)

    }

    override fun prepareFailView(throwable: Throwable)  = run {responseEntity = createErrorResponseFrom(throwable)}


    override fun isDone() = responseEntity != null


    data class ViewModel(
        val userId: UUID,
        val systematicStudyId: UUID,
        val formattedReview: String
    ): RepresentationModel<ViewModel>()
}
