package br.all.domain.model.study

import br.all.domain.common.ddd.Notification
import br.all.domain.common.ddd.ValueObject
import java.util.UUID

class Answer <T> (questionId: UUID, value: T) : ValueObject() {
    override fun validate(): Notification {
        return Notification()
    }
}