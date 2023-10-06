package br.all.domain.model.protocol

import br.all.domain.shared.ddd.Notification
import br.all.domain.shared.ddd.ValueObject

data class ResearchQuestion(val description: String) : ValueObject() {
    override fun validate(): Notification {
        TODO("Not yet implemented")
    }
}
