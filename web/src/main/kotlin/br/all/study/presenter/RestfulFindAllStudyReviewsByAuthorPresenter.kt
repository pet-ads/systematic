package br.all.study.presenter

import br.all.application.study.find.presenter.FindAllStudyReviewsByAuthorPresenter
import br.all.application.study.find.service.FindAllStudyReviewsByAuthorService
import br.all.application.study.repository.StudyReviewDto
import br.all.shared.error.createErrorResponseFrom
import br.all.utils.LinksFactory
import org.springframework.hateoas.RepresentationModel
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.util.*

class RestfulFindAllStudyReviewsByAuthorPresenter(
    private val linksFactory: LinksFactory
) : FindAllStudyReviewsByAuthorPresenter {

    var responseEntity: ResponseEntity<*>? = null

    override fun prepareSuccessView(response: FindAllStudyReviewsByAuthorService.ResponseModel) {
        val (_, systematicStudyId, studyReviews) = response
        val restfulResponse = ViewModel(systematicStudyId, studyReviews.size, studyReviews)

        responseEntity = ResponseEntity.status(HttpStatus.OK).body(restfulResponse)
    }

    override fun prepareFailView(throwable: Throwable) {
        responseEntity = createErrorResponseFrom(throwable)
    }

    override fun isDone() = responseEntity != null

    private data class ViewModel(
        val systematicStudyId: UUID,
        val size: Int,
        val studyReviews: List<StudyReviewDto>,
    ) : RepresentationModel<ViewModel>()
}
