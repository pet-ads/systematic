package br.all.study.presenter

import br.all.application.study.find.presenter.FindStudyReviewPresenter
import br.all.application.study.find.service.FindStudyReviewService.ResponseModel
import br.all.application.study.repository.StudyReviewDto
import br.all.shared.error.createErrorResponseFrom
import br.all.study.controller.StudyReviewController
import org.springframework.hateoas.RepresentationModel
import org.springframework.hateoas.server.mvc.linkTo
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component

@Component
class RestfulFindStudyReviewPresenter : FindStudyReviewPresenter {

    var responseEntity: ResponseEntity<*>? = null

    override fun prepareSuccessView(response: ResponseModel) {
        val restfulResponse = ViewModel(response.content)

        val self = linkTo<StudyReviewController> {
            findStudyReview(response.researcherId, response.content.systematicStudyId, response.content.studyReviewId)
        }.withSelfRel()

        restfulResponse.add(self)
        responseEntity = ResponseEntity.status(HttpStatus.OK).body(restfulResponse)
    }

    override fun prepareFailView(throwable: Throwable)= run { responseEntity = createErrorResponseFrom(throwable) }

    override fun isDone() = responseEntity != null

    private data class ViewModel(private val content: StudyReviewDto) : RepresentationModel<ViewModel>() {
        val studyReviewId = content.studyReviewId
        val systematicStudyId = content.systematicStudyId
        val studyType = content.studyType
        val title = content.title
        val year = content.year
        val authors = content.authors
        val venue = content.venue
        val abstract = content.abstract
        val keywords = content.keywords
        val references = content.references
        val doi = content.doi
        val searchSources = content.searchSources
        val criteria = content.criteria
        val formAnswers = content.formAnswers
        val qualityAnswers = content.robAnswers
        val comments = content.comments
        val readingPriority = content.readingPriority
        val extractionStatus = content.extractionStatus
        val selectionStatus = content.selectionStatus
    }
}