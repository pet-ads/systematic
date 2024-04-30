package br.all.study.presenter

import br.all.application.study.create.CreateStudyReviewService
import br.all.application.study.find.presenter.FindAllStudyReviewsBySourcePresenter
import br.all.application.study.find.service.FindAllStudyReviewsBySourceService.ResponseModel
import br.all.application.study.repository.StudyReviewDto
import br.all.shared.error.createErrorResponseFrom
import br.all.study.controller.StudyReviewController
import org.springframework.hateoas.RepresentationModel
import org.springframework.hateoas.server.mvc.linkTo
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import java.util.*

@Component
class RestfulFindAllStudyReviewsBySourcePresenter : FindAllStudyReviewsBySourcePresenter {

    var responseEntity: ResponseEntity<*>? = null

    override fun prepareSuccessView(response: ResponseModel) {
        val (researcherId, systematicStudyId, searchSource, studyReviews) = response
        val restfulResponse = ViewModel(systematicStudyId, searchSource, studyReviews.size, studyReviews)

        val selfRef = linkSelfRef(response)
        val allStudyReview = linkFindAllStudiesReviews(response)
        val createStudyReview = linkCreateStudyReview(response)

        restfulResponse.add(selfRef, allStudyReview, createStudyReview)
        responseEntity = ResponseEntity.status(HttpStatus.OK).body(restfulResponse)
    }

    private fun linkSelfRef(response: ResponseModel) =
        linkTo<StudyReviewController> {
            findAllStudyReviewsBySource(response.researcherId, response.systematicStudyId, response.searchSource)
        }.withSelfRel()

    private fun linkFindAllStudiesReviews(response: ResponseModel) =
        linkTo<StudyReviewController> {
            findAllStudyReviews(response.researcherId, response.systematicStudyId)
        }.withRel("allStudyReview")

    private fun linkCreateStudyReview(response: ResponseModel) =
        linkTo<StudyReviewController> {
            createStudyReview(
                response.researcherId,
                response.systematicStudyId,
                request = CreateStudyReviewService.RequestModel(
                    researcherId = response.researcherId,
                    systematicStudyId = response.systematicStudyId,
                    type = "",
                    title = "",
                    year = 2024,
                    authors = "",
                    venue = "",
                    abstract = "",
                    keywords = emptySet(),
                    source = ""
                )
            )
        }.withRel("createStudyReview")

    override fun prepareFailView(throwable: Throwable) = run { responseEntity = createErrorResponseFrom(throwable) }

    override fun isDone() = responseEntity != null

    private data class ViewModel(
        val systematicStudyId: UUID,
        val searchSource: String,
        val size: Int,
        val studyReviews: List<StudyReviewDto>,
    ) : RepresentationModel<ViewModel>()
}
