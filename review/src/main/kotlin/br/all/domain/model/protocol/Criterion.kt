package br.all.domain.model.protocol

import br.all.domain.shared.ddd.Notification
import br.all.domain.shared.ddd.ValueObject
import jakarta.persistence.Embeddable

data class Criterion internal constructor(
    val description: String,
    val type: CriterionType,
) : ValueObject() {
    init {
        val notification = validate()
        require(notification.hasNoErrors()) { notification.message() }
    }

    override fun validate() = Notification().also {
        if (description.isBlank()) it.addError("The criterion description cannot be blank!")
    }

    companion object {
        fun toInclude(description: String) = Criterion(description, CriterionType.INCLUSION)

        fun toExclude(description: String) = Criterion(description, CriterionType.EXCLUSION)
    }

    enum class CriterionType { INCLUSION, EXCLUSION }
}
