package br.all.question.controller

import br.all.application.question.create.CreateQuestionService
import br.all.application.question.find.FindQuestionService
import br.all.domain.model.question.QuestionId
import br.all.question.presenter.riskOfBias.RestfulCreateQuestionPresenter
import br.all.question.presenter.riskOfBias.RestfulFindQuestionPresenter
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*
import br.all.application.question.create.CreateQuestionService.RequestModel as CreateRequest

@RestController
@RequestMapping("/api/v1/researcher/{researcherId}/systematic-study/{systematicStudyId}/protocol/extraction-question")
class ExtractionQuestionController(
    val createService: CreateQuestionService,
    val findOneService: FindQuestionService
){
    @PostMapping("/{questionType}")
     fun createQuestion(
        @PathVariable researcherId: UUID,
        @PathVariable systematicStudyId: UUID,
        @PathVariable questionType: String,
        @RequestBody request: CreateRequest,
    ): ResponseEntity<*> {
        val presenter = RestfulCreateQuestionPresenter()
        createService.createExtractionQuestion(presenter, request)

        return presenter.responseEntity?: ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @GetMapping("/{code}")
     fun findQuestion(
        @PathVariable researcherId: UUID,
        @PathVariable systematicStudyId: UUID,
        @PathVariable questionId: QuestionId,
    ): ResponseEntity<*> {
        val presenter = RestfulFindQuestionPresenter()
        val request = FindQuestionService.RequestModel(researcherId, systematicStudyId, questionId)
        findOneService.findOne(presenter, request)
        return presenter.responseEntity?: ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR)
    }
}