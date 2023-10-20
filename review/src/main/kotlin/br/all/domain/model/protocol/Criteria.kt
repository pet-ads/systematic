package br.all.domain.model.protocol

import br.all.domain.shared.ddd.Notification
import br.all.domain.shared.ddd.ValueObject

data class Criteria(
    val description: String,
    val type: CriteriaType,
) : ValueObject() {
    enum class CriteriaType { INCLUSION, EXCLUSION }

    init {
        val notification = validate()
        require(notification.hasNoErrors()) { notification.message() }
    }

    override fun validate(): Notification {
        val notification = Notification()
        val pattern = Regex("^(([^a-z]+ )?[a-z]+( ?| [^a-z]+( |$)?))+$", RegexOption.IGNORE_CASE)

        if (!pattern.matches(description))
            notification.addError("Wrong criteria format! Provided: \"$description\"")

        return notification
    }
}
