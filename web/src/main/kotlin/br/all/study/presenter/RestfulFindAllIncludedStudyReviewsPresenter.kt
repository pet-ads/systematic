package br.all.study.presenter

import br.all.application.study.find.presenter.FindAllIncludedStudyReviewsPresenter
import br.all.application.study.find.service.FindAllIncludedStudyReviewsService
import br.all.application.study.repository.StudyReviewDto
import br.all.shared.error.createErrorResponseFrom
import br.all.utils.LinksFactory
import org.springframework.hateoas.RepresentationModel
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.util.*

class RestfulFindAllIncludedStudyReviewsPresenter (
    private val linksFactory: LinksFactory
): FindAllIncludedStudyReviewsPresenter {

    var responseEntity: ResponseEntity<*>? = null

    override fun prepareSuccessView(response: FindAllIncludedStudyReviewsService.ResponseModel) {
        val (
            _,
            systematicStudyId,
            studyReviews,
            page,
            size,
            totalElements,
            totalPages
        ) = response

        val restfulResponse = ViewModel(
            systematicStudyId = systematicStudyId,
            size = studyReviews.size,
            studyReviews = studyReviews,
            page = page,
            totalElements = totalElements,
            totalPages = totalPages
        )

        val selfRef = linksFactory.findAllIncludedStudies(
            systematicStudyId,
            page,
            size
        )

        if (totalPages > 0) {
            restfulResponse.add(linksFactory.findAllIncludedStudiesFirstPage(
                systematicStudyId,
                size
            ))
            restfulResponse.add(linksFactory.findAllIncludedStudiesLastPage(
                systematicStudyId,
                totalPages,
                size
            ))

            if (page < totalPages - 1) {
                restfulResponse.add(linksFactory.findAllIncludedStudiesNextPage(
                    systematicStudyId,
                    page,
                    size
                ))
            }

            if (page > 0) {
                restfulResponse.add(linksFactory.findAllIncludedStudiesPrevPage(
                    systematicStudyId,
                    page,
                    size
                ))
            }
        }

        restfulResponse.add(selfRef)

        responseEntity = ResponseEntity.status(HttpStatus.OK).body(restfulResponse)
    }

    override fun prepareFailView(throwable: Throwable) = run { responseEntity = createErrorResponseFrom(throwable) }

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