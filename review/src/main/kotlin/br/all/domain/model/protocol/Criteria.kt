package br.all.domain.model.protocol

import br.all.domain.shared.ddd.Notification
import br.all.domain.shared.ddd.ValueObject

data class Criteria internal constructor(
    val description: String,
    val type: CriteriaType,
) : ValueObject() {
    enum class CriteriaType { INCLUSION, EXCLUSION }

    companion object {
        fun toInclude(description: String) = Criteria(description, CriteriaType.INCLUSION)

        fun toExclude(description: String) = Criteria(description, CriteriaType.EXCLUSION)
    }

    override fun validate() = Notification()
}
