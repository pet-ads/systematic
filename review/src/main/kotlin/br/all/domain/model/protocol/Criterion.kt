package br.all.domain.model.protocol

import br.all.domain.shared.ddd.Notification
import br.all.domain.shared.ddd.ValueObject

data class Criterion internal constructor(
    val description: String,
    val type: CriterionType,
) : ValueObject() {
    enum class CriterionType { INCLUSION, EXCLUSION }

    companion object {
        fun toInclude(description: String) = Criterion(description, CriterionType.INCLUSION)

        fun toExclude(description: String) = Criterion(description, CriterionType.EXCLUSION)
    }

    override fun validate() = Notification()
}
