package br.all.study.presenter

import br.all.application.study.find.presenter.FindAllStudyReviewsPresenter
import br.all.application.study.find.service.FindAllStudyReviewsService.ResponseModel
import br.all.application.study.repository.StudyReviewDto
import br.all.shared.error.createErrorResponseFrom
import br.all.utils.LinksFactory
import org.springframework.hateoas.RepresentationModel
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import java.util.*

@Component
class RestfulFindAllStudyReviewsPresenter(
    private val linksFactory: LinksFactory
) : FindAllStudyReviewsPresenter {

    var responseEntity: ResponseEntity<*>? = null

    override fun prepareSuccessView(response: ResponseModel) {
        val restfulResponse = ViewModel(response.systematicStudyId, response.studyReviews.size, response.studyReviews)

        val selfRef = linksFactory.findAllStudies(response.systematicStudyId)
        val sources = getSources(response.studyReviews)
        for (source in sources) {
            restfulResponse.add(linksFactory.findAllStudiesBySource(response.systematicStudyId, source))
        }
        val createStudyReview = linksFactory.createStudy(response.systematicStudyId)

        restfulResponse.add(selfRef, createStudyReview)
        responseEntity = ResponseEntity.status(HttpStatus.OK).body(restfulResponse)
    }

    override fun prepareFailView(throwable: Throwable) = run {responseEntity = createErrorResponseFrom(throwable) }

    override fun isDone() = responseEntity != null

    fun getSources(studies: List<StudyReviewDto>): List<String> = studies.flatMap { it.searchSources }.distinct()

    private data class ViewModel (
        val systematicStudyId : UUID,
        val size: Int,
        val studyReviews: List<StudyReviewDto>,
    ) : RepresentationModel<ViewModel>()
}