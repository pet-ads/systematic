package br.all.question.controller

import br.all.domain.model.question.QuestionId
import org.apache.coyote.Response
import br.all.application.question.create.CreateQuestionService.RequestModel as CreateRequest
import org.springframework.http.ResponseEntity
import java.util.UUID

interface QuestionController {
    fun createQuestion(
        researcherId: UUID,
        systematicStudyId: UUID,
        request: CreateRequest
    ): ResponseEntity<*>

    fun findQuestion(
        researcherId: UUID,
        systematicStudyId: UUID,
        questionId: QuestionId
    ): ResponseEntity<*>
}