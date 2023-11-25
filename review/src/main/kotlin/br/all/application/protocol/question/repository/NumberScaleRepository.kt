package br.all.application.protocol.question.repository

import br.all.application.protocol.question.create.numberScale.NumberScaleDTO
import br.all.domain.model.protocol.question.QuestionId

interface NumberScaleRepository {
    fun create(numberScaleDTO: NumberScaleDTO)

    fun findById(numberScaleId: QuestionId) : NumberScaleDTO

    fun update(numberScaleDTO: NumberScaleDTO)
}