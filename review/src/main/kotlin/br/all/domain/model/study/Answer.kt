package br.all.domain.model.study

import br.all.domain.shared.ddd.Notification
import br.all.domain.shared.ddd.ValueObject
import java.util.*

data class Answer <T> (val questionId: UUID, val value: T) : ValueObject() {
    override fun validate() =  Notification()
}