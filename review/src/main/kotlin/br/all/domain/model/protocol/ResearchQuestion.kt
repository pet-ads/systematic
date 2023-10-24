package br.all.domain.model.protocol

import br.all.domain.shared.ddd.Notification
import br.all.domain.shared.ddd.ValueObject
import br.all.domain.shared.utils.phrase.Phrase

data class ResearchQuestion(val description: Phrase) : ValueObject() {
    override fun validate() = Notification()
}
