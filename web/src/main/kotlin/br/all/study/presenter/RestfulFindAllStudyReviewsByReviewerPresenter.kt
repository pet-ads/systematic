package br.all.study.presenter

import br.all.application.study.find.presenter.FindAllStudyReviewsByReviewerPresenter
import br.all.application.study.find.service.FindAllStudyReviewsByReviewerService.ResponseModel
import br.all.application.study.repository.StudyReviewDto
import br.all.domain.model.study.StudyReviewStage
import br.all.shared.error.createErrorResponseFrom
import br.all.utils.LinksFactory
import org.springframework.hateoas.RepresentationModel
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.util.*

class RestfulFindAllStudyReviewsByReviewerPresenter(
    private val linksFactory: LinksFactory
) : FindAllStudyReviewsByReviewerPresenter {

    var responseEntity: ResponseEntity<*>? = null

    override fun prepareSuccessView(response: ResponseModel) {
        val restfulResponse = ViewModel(
            systematicStudyId = response.systematicStudyId,
            reviewerId = response.reviewerId,
            stage = response.stage,
            size = response.studyReviews.size,
            studyReviews = response.studyReviews,
            page = response.page,
            totalElements = response.totalElements,
            totalPages = response.totalPages
        )
        restfulResponse.add(linksFactory.findAllStudiesByReviewer(
            response.systematicStudyId, response.reviewerId, response.stage, response.page, response.size
        ))
        responseEntity = ResponseEntity.status(HttpStatus.OK).body(restfulResponse)
    }

    override fun prepareFailView(throwable: Throwable) = run { responseEntity = createErrorResponseFrom(throwable) }

    override fun isDone() = responseEntity != null

    private data class ViewModel(
        val systematicStudyId: UUID,
        val reviewerId: UUID,
        val stage: StudyReviewStage?,
        val size: Int,
        val studyReviews: List<StudyReviewDto>,
        val page: Int,
        val totalElements: Long,
        val totalPages: Int
    ) : RepresentationModel<ViewModel>()
}