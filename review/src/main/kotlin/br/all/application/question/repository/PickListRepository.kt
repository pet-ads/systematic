package br.all.application.question.repository

import br.all.application.question.create.pickList.PickListDTO
import br.all.domain.model.question.QuestionId

interface PickListRepository {
    fun create(pickListDTO: PickListDTO)

    fun findById(pickListId: QuestionId) : PickListDTO

    fun update(pickListDTO: PickListDTO)
}