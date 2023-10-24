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
        if (groupingSignsAreNotCorrectlyClose())
            notification.addError("Parenthesis, brackets and/or braces should be closed appropriately!")
        if (textHasDigitsAndSymbolsWithinNotQuotedWords())
            notification.addError("Symbols should not be within not quoted words in a phrase. " +
                    "Provided: $text")

        return notification
    }

    private fun textHasDigitsAndSymbolsWithinNotQuotedWords() : Boolean {
        val notQuotedWords = Regex("\"[^\"]+\"|'[^']+'").split(text)

        val notAllowedSymbolsWithin = "[a-z]*[^a-z0-9'\\- ]+[a-z]+"
        val notAllowedSymbolsAfter = "[a-z]+[^a-z0-9,.;:?\\]})!'\\- ]+"
        val startsOrEndsWithHyphenOrApostrophe = "[a-z]+[-']([^a-z0-9 ]+|\\$)|(^|[^a-z0-9])[-'][a-z]+"
        val pattern = Regex("$notAllowedSymbolsWithin|$notAllowedSymbolsAfter|" +
                startsOrEndsWithHyphenOrApostrophe, RegexOption.IGNORE_CASE)

        return notQuotedWords.any { pattern.containsMatchIn(it) }
    }

    private fun groupingSignsAreNotCorrectlyClose() : Boolean {
        val openingSigns = setOf('(', '[', '{')
        val closingSigns = setOf(')', ']', '}')
        val signs = mutableListOf<Char>()

        for (c in text) {
            if (c in openingSigns) {
                signs.add(c)
                continue
            }
            if (c !in closingSigns) continue
            if (signs.isEmpty()) return true

            val lastOpeningSing = signs.last()
            if (closingSigns.indexOf(c) != openingSigns.indexOf(lastOpeningSing)) return true

            signs.removeLast()
        }
        return false
    }
}