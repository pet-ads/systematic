package br.all.question.presenter.riskOfBias

import br.all.application.question.create.CreateQuestionPresenter
import br.all.application.question.create.CreateQuestionService.*
import br.all.question.controller.RiskOfBiasQuestionController
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
class RestfulCreateRoBQuestionPresenter(
    private val linksFactory: LinksFactory
) : CreateQuestionPresenter {
    var responseEntity: ResponseEntity<*>? = null
    override fun prepareSuccessView(response: ResponseModel) {
        val restfulResponse = ViewModel(response.userId, response.systematicStudyId, response.questionId)

        val selfRef = linksFactory.findRobQuestion(response.systematicStudyId, response.questionId)
        val pickList = linksFactory.createPickListRobQuestion(response.systematicStudyId)
        val pickMany = linksFactory.createPickManyRobQuestion(response.systematicStudyId)
        val labeledScale = linksFactory.createLabeledScaleRobQuestion(response.systematicStudyId)
        val numberScale = linksFactory.createNumberScaleRobQuestion(response.systematicStudyId)
        val findAll = linksFactory.findAllReviewRobQuestions(response.systematicStudyId)

        restfulResponse.add(selfRef, pickList, pickMany, labeledScale, numberScale, findAll)
        responseEntity = status(HttpStatus.CREATED).body(restfulResponse)
    }

    override fun prepareFailView(throwable: Throwable) = run {responseEntity = createErrorResponseFrom(throwable) }
    override fun isDone() = responseEntity != null
    private data class ViewModel(
        val userId: UUID,
        val systematicStudyId: UUID,
        val questionId: UUID
    ): RepresentationModel<ViewModel>()


}
