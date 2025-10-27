package br.all.study.presenter

import br.all.application.study.find.presenter.FindAllStudyReviewsByAdvancedSearchPresenter
import br.all.application.study.find.service.FindAllStudyReviewsByAdvancedSearchService
import br.all.application.study.repository.StudyReviewDto
import br.all.shared.error.createErrorResponseFrom
import br.all.utils.LinksFactory
import org.springframework.hateoas.RepresentationModel
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.util.*

class RestfulFindAllStudyReviewsByAdvancedSearchPresenter(
    private val linksFactory: LinksFactory
) : FindAllStudyReviewsByAdvancedSearchPresenter {

    var responseEntity: ResponseEntity<*>? = null

    override fun prepareSuccessView(response: FindAllStudyReviewsByAdvancedSearchService.ResponseModel) {
        val restfulResponse = ViewModel(
            systematicStudyId = response.systematicStudyId,
            size = response.studyReviews.size,
            studyReviews = response.studyReviews,
            page = response.page,
            totalElements = response.totalElements,
            totalPages = response.totalPages
        )

        restfulResponse.add(
            linksFactory.findAllIncludedStudies(response.systematicStudyId, response.page, response.size)
        )

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
        val page: Int,
        val totalElements: Long,
        val totalPages: Int
    ) : RepresentationModel<ViewModel>()
}
