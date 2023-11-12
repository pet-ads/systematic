package br.all.domain.model.protocol

import br.all.domain.shared.ddd.Notification
import br.all.domain.shared.ddd.ValueObject
import br.all.domain.shared.utils.phrase.Phrase

data class Criteria internal constructor(
    val description: Phrase,
    val type: CriteriaType,
) : ValueObject() {
    enum class CriteriaType { INCLUSION, EXCLUSION }

    companion object {
        fun toInclude(description: Phrase) = Criteria(description, CriteriaType.INCLUSION)

        fun toExclude(description: Phrase) = Criteria(description, CriteriaType.EXCLUSION)
    }

    override fun validate() = Notification()
}
