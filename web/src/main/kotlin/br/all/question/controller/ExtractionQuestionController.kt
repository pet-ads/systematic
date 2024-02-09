package br.all.question.controller

import br.all.application.question.create.CreateQuestionService
import br.all.application.question.create.CreateQuestionService.QuestionType.*
import br.all.application.question.create.CreateQuestionService.RequestModel
import br.all.application.question.find.FindQuestionService
import br.all.question.presenter.extraction.RestfulFindExtractionQuestionPresenter
import br.all.question.presenter.extraction.RestfulCreateExtractionQuestionPresenter
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*
import br.all.application.question.create.CreateQuestionService.RequestModel as CreateRequest

@RestController
@RequestMapping("/api/v1/researcher/{researcherId}/systematic-study/{systematicStudyId}/protocol/extraction-question")
class ExtractionQuestionController(
    val createQuestionService: CreateQuestionService, val findOneService: FindQuestionService
) {
    data class TextualRequest(val code: String, val description: String)
    data class PickListRequest(val code: String, val description: String, val options: List<String>)
    data class LabeledScaleRequest(val code: String, val description: String, val scales: Map<String, Int>)
    data class NumberScaleRequest(val code: String, val description: String, val lower: Int, val higher: Int)

    fun createQuestion(request: CreateRequest): ResponseEntity<*> {
        val presenter = RestfulCreateExtractionQuestionPresenter()
        createQuestionService.create(presenter, request)
        return presenter.responseEntity ?: ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @PostMapping("/textual")
    fun createTextualQuestion(
        @PathVariable researcherId: UUID, @PathVariable systematicStudyId: UUID, @RequestBody request: TextualRequest,
    ): ResponseEntity<*> = createQuestion(
        RequestModel(
            researcherId,
            systematicStudyId,
            TEXTUAL,
            request.code,
            request.description
        )
    )

    @PostMapping("/pick-list")
    fun createPickListQuestion(
        @PathVariable researcherId: UUID, @PathVariable systematicStudyId: UUID, @RequestBody request: PickListRequest,
    ): ResponseEntity<*> = createQuestion(
        RequestModel(
            researcherId,
            systematicStudyId,
            PICK_LIST,
            request.code,
            request.description,
            options = request.options
        )
    )

    @PostMapping("/labeled-scale")
    fun createLabeledScaleQuestion(
        @PathVariable researcherId: UUID,
        @PathVariable systematicStudyId: UUID,
        @RequestBody request: LabeledScaleRequest,
    ): ResponseEntity<*> = createQuestion(
        RequestModel(
            researcherId,
            systematicStudyId,
            LABELED_SCALE,
            request.code,
            request.description,
            scales = request.scales
        )
    )

    @PostMapping("/number-scale")
    fun createNumberScaleQuestion(
        @PathVariable researcherId: UUID,
        @PathVariable systematicStudyId: UUID,
        @RequestBody request: NumberScaleRequest,
    ): ResponseEntity<*> = createQuestion(
        RequestModel(
            researcherId,
            systematicStudyId,
            NUMBERED_SCALE,
            request.code,
            request.description,
            higher = request.higher,
            lower = request.lower
        )
    )

    @GetMapping("/{questionId}")
    fun findQuestion(
        @PathVariable researcherId: UUID,
        @PathVariable systematicStudyId: UUID,
        @PathVariable questionId: UUID,
    ): ResponseEntity<*> {
        val presenter = RestfulFindExtractionQuestionPresenter()
        val request = FindQuestionService.RequestModel(researcherId, systematicStudyId, questionId)
        findOneService.findOne(presenter, request)
        return presenter.responseEntity ?: ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR)
    }
}