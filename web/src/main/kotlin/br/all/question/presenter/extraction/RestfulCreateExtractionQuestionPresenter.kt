package br.all.question.presenter.extraction

import br.all.application.question.create.CreateQuestionPresenter
import br.all.application.question.create.CreateQuestionService.*
import br.all.question.controller.ExtractionQuestionController
import br.all.shared.error.createErrorResponseFrom
import br.all.utils.LinksFactory
import org.springframework.hateoas.RepresentationModel
import org.springframework.hateoas.server.mvc.linkTo
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.*
import org.springframework.stereotype.Component
import java.util.*

@Component
class RestfulCreateExtractionQuestionPresenter(
    private val linksFactory: LinksFactory
) : CreateQuestionPresenter {
    var responseEntity: ResponseEntity<*>? = null

    override fun prepareSuccessView(response: ResponseModel) {
        val viewModel = ViewModel(response.userId, response.systematicStudyId, response.questionId)

        val selfRef = linksFactory.findExtractionQuestion(response.systematicStudyId, response.questionId)
        val pickList = linksFactory.createPickListExtractionQuestion(response.systematicStudyId)
        val pickMany = linksFactory.createPickManyExtractionQuestion(response.systematicStudyId)
        val labeledScale = linksFactory.createLabeledScaleExtractionQuestion(response.systematicStudyId)
        val numberScale = linksFactory.createNumberScaleExtractionQuestion(response.systematicStudyId)
        val findAll = linksFactory.findAllReviewExtractionQuestions(response.systematicStudyId)

        viewModel.add(selfRef, pickList, pickMany, labeledScale, numberScale, findAll)
        responseEntity = status(HttpStatus.CREATED).body(viewModel)
    }

    override fun prepareFailView(throwable: Throwable) = run { responseEntity = createErrorResponseFrom(throwable) }

    override fun isDone() = responseEntity != null

    private data class ViewModel(
        val userId: UUID,
        val systematicStudyId: UUID,
        val questionId: UUID
    ) : RepresentationModel<ViewModel>()
}
