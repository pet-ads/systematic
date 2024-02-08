package br.all.question.controller

import br.all.application.question.create.CreateQuestionService
import br.all.application.question.find.FindQuestionService
import br.all.question.presenter.riskOfBias.RestfulCreateRoBQuestionPresenter
import br.all.question.presenter.riskOfBias.RestfulFindRoBQuestionPresenter
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*
import br.all.application.question.create.CreateQuestionService.RequestModel as CreateRequest

@RestController
@RequestMapping("/api/v1/researcher/{researcherId}/systematic-study/{systematicStudyId}/protocol/extraction-question")
class ExtractionQuestionController(
    val createQuestionService: CreateQuestionService,
    val findOneService: FindQuestionService
) {
    @PostMapping
    fun createQuestion(
        @PathVariable researcherId: UUID,
        @PathVariable systematicStudyId: UUID,
        @RequestBody request: CreateRequest,
    ): ResponseEntity<*> {
        val presenter = RestfulCreateRoBQuestionPresenter()
        createQuestionService.create(presenter, request)

        return presenter.responseEntity ?: ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @GetMapping("/{code}")
    fun findQuestion(
        @PathVariable researcherId: UUID,
        @PathVariable systematicStudyId: UUID,
        @PathVariable questionId: UUID,
    ): ResponseEntity<*> {
        val presenter = RestfulFindRoBQuestionPresenter()
        val request = FindQuestionService.RequestModel(researcherId, systematicStudyId, questionId)
        findOneService.findOne(presenter, request)
        return presenter.responseEntity ?: ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR)
    }
}