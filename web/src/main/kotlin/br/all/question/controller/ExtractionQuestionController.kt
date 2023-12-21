package br.all.question.controller

import br.all.application.question.create.CreateQuestionService
import br.all.application.question.find.FindQuestionService
import br.all.domain.model.question.QuestionId
import br.all.application.question.create.CreateQuestionService.RequestModel as CreateRequest
import br.all.question.presenter.RestfulCreateQuestionPresenter
import br.all.question.presenter.RestfulFindQuestionPresenter
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/v1/researcher/{researcherId}/systematic-study/{systematicStudyId}/protocol/extraction-question")
class ExtractionQuestionController(
    val createService: CreateQuestionService,
    val findOneService: FindQuestionService
) : QuestionController {
    @PostMapping
    override fun createQuestion(
        @PathVariable researcherId: UUID,
        @PathVariable systematicStudyId: UUID,
        @RequestBody request: CreateRequest
    ): ResponseEntity<*> {
        val presenter = RestfulCreateQuestionPresenter()
        createService.createExtractionQuestion(presenter, request)

        return presenter.responseEntity?: ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @GetMapping("/{code}")
    override fun findQuestion(
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