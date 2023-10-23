package br.all.domain.shared.utils.phrase

import br.all.domain.shared.ddd.Notification
import br.all.domain.shared.ddd.ValueObject

data class Phrase(val text: String) : ValueObject() {
    init {
        val notification = validate()
        require(notification.hasNoErrors()) { notification.message() }
    }

    override fun validate(): Notification {
        val notification = Notification()

        if (text.isBlank())
            notification.addError("A phrase must not be blank!")
        if (textHasDigitsAndSymbolsWithinNotQuotedWords())
            notification.addError("Symbols should not be within not quoted words in a phrase. " +
                    "Provided: $text")

        return notification
    }

    private fun textHasDigitsAndSymbolsWithinNotQuotedWords() : Boolean {
        val notQuotedWords = Regex("\"[^\"]+\"|'[^']+'").split(text)
        val pattern = Regex("[a-z]*[^a-z0-9 ]+[a-z]+|[a-z]+[^a-z0-9 ]+", RegexOption.IGNORE_CASE)

        return notQuotedWords.any { pattern.containsMatchIn(it) }
    }
}