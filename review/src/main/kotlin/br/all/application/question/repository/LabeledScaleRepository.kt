package br.all.application.protocol.question.repository

import br.all.application.question.create.labeledScale.LabeledScaledDTO
import br.all.domain.model.question.QuestionId

interface LabeledScaleRepository {
    fun create(labeledScaledDTO: LabeledScaledDTO)

    fun findById(labeledScaleId: QuestionId) : LabeledScaledDTO

    fun update(labeledScaledDTO: LabeledScaledDTO)
}