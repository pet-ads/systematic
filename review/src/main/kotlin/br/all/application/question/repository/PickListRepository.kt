package br.all.application.protocol.question.repository

import br.all.application.question.create.pickList.PickListDTO
import br.all.domain.model.question.QuestionId

interface PickListRepository {
    fun create(pickListDTO: PickListDTO)

    fun findById(pickListId: QuestionId) : PickListDTO

    fun update(pickListDTO: PickListDTO)
}