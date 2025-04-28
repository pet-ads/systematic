package br.all.report.presenter

import br.all.application.report.find.presenter.IncludedStudiesAnswersPresenter
import br.all.application.report.find.service.IncludedStudiesAnswersService.QuestionWithAnswer
import br.all.application.report.find.service.IncludedStudiesAnswersService.ResponseModel
import br.all.shared.error.createErrorResponseFrom
import br.all.utils.LinksFactory
import org.springframework.hateoas.RepresentationModel
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.util.*

class RestfulIncludedStudiesAnswersPresenter(
    private val linksFactory: LinksFactory
): IncludedStudiesAnswersPresenter {
    var responseEntity: ResponseEntity<*>? = null

    override fun prepareSuccessView(response: ResponseModel) {
        val restfulFindAnswersResponse = ViewModel(
            response.userId,
            response.systematicStudyId,
            response.studyReviewId,
            response.year,
            response.includedBy,
            response.extractionQuestions,
            response.robQuestions,
        )

        val selfRef = linksFactory.includedStudiesAnswers(response.systematicStudyId, response.studyReviewId)

        restfulFindAnswersResponse.add(selfRef)
        responseEntity = ResponseEntity.status(HttpStatus.OK).body(restfulFindAnswersResponse)
    }

    override fun prepareFailView(throwable: Throwable) = run {responseEntity = createErrorResponseFrom(throwable)}

    override fun isDone() = responseEntity != null



    data class ViewModel(
        val userId: UUID,
        val systematicStudyId: UUID,
        val studyReviewId: Long,
        val year: Int,
        val includedBy: Set<String>,
        val extractionQuestions: List<QuestionWithAnswer>,
        val robQuestions: List<QuestionWithAnswer>
    ): RepresentationModel<ViewModel>()
}