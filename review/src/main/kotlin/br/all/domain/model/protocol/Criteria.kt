package br.all.domain.model.protocol

import br.all.domain.shared.ddd.Notification
import br.all.domain.shared.ddd.ValueObject
import br.all.domain.shared.utils.phrase.Phrase

data class Criteria(
    val description: Phrase,
    val type: CriteriaType,
) : ValueObject() {
    enum class CriteriaType { INCLUSION, EXCLUSION }

    override fun validate() = Notification()
}
