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

        if (description.isBlank())
            notification.addError("A criteria cannot have a blank description!")
        if (descriptionHasDigitsAndSymbolsWithinNotQuotedWords())
            notification.addError("Symbols and numbers should be within not quoted words in criteria " +
                    "description. Provided: $description")

        return notification
    }

    private fun descriptionHasDigitsAndSymbolsWithinNotQuotedWords() : Boolean {
        val notQuotedWords = Regex("\"[^\"]+\"|'[^']+'").split(description)
        val pattern = Regex("[a-z]*[^a-z ]+[a-z]+|[a-z]+[^a-z ]+", RegexOption.IGNORE_CASE)

        return notQuotedWords.any { pattern.containsMatchIn(it) }
    }
}
