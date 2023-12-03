package br.all.application.question.repository

import br.all.application.protocol.question.create.numberScale.NumberScaleDTO
import br.all.domain.model.question.QuestionId

interface NumberScaleRepository {
    fun create(numberScaleDTO: NumberScaleDTO)

    fun findById(numberScaleId: QuestionId) : NumberScaleDTO

    fun update(numberScaleDTO: NumberScaleDTO)
}