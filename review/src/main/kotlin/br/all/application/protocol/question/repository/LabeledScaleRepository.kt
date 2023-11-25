package br.all.application.protocol.question.repository

import br.all.application.protocol.question.create.labeledScale.LabeledScaledDTO
import br.all.domain.model.protocol.question.QuestionId
import java.util.UUID

interface LabeledScaleRepository {
    fun create(labeledScaledDTO: LabeledScaledDTO)

    fun findById(labeledScaleId: QuestionId) : LabeledScaledDTO

    fun update(labeledScaledDTO: LabeledScaledDTO)
}