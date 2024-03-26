package br.all.study.presenter

import br.all.application.study.create.CreateStudyReviewService
import br.all.application.study.find.presenter.FindAllStudyReviewsPresenter
import br.all.application.study.find.service.FindAllStudyReviewsService.ResponseModel
import br.all.application.study.repository.StudyReviewDto
import br.all.shared.error.createErrorResponseFrom
import br.all.study.controller.StudyReviewController
import org.springframework.hateoas.RepresentationModel
import org.springframework.hateoas.server.mvc.linkTo
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import java.util.*

//O que faz sentido para mim:
//findAllStudyReviewsBySource - OK
//createStudyReview - OK

@Component
class RestfulFindAllStudyReviewsPresenter : FindAllStudyReviewsPresenter {

    var responseEntity: ResponseEntity<*>? = null

    override fun prepareSuccessView(response: ResponseModel) {
        val restfulResponse = ViewModel(response.systematicStudyId, response.studyReviews.size, response.studyReviews)
    }

    override fun prepareFailView(throwable: Throwable) = run {responseEntity = createErrorResponseFrom(throwable) }

    override fun isDone() = responseEntity != null

    private data class ViewModel (
        val systematicStudyId : UUID,
        val size: Int,
        val studyReviews: List<StudyReviewDto>,
    ) : RepresentationModel<ViewModel>()


    private fun prepareHateoas(response: ResponseModel, restfulResponse: ViewModel) {

        val self = linkTo<StudyReviewController> {
            findAllStudyReviews(response.researcherId, response.systematicStudyId)
        }.withSelfRel()


        val findAllBySource = linkTo<StudyReviewController> {
            findAllStudyReviewsBySource(response.researcherId, response.systematicStudyId, searchSource = "")
        }.withRel("findAllBySource")


        val createStudyReview = linkTo<StudyReviewController> {
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

        restfulResponse.add(self, findAllBySource, createStudyReview)
        responseEntity = ResponseEntity.status(HttpStatus.OK).body(restfulResponse)
    }
}