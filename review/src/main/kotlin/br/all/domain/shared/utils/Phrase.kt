package br.all.domain.shared.utils

import br.all.domain.shared.ddd.Notification
import br.all.domain.shared.ddd.ValueObject

data class Phrase(private val text: String) : ValueObject() {
    init {
        val notification = validate()
        require(notification.hasNoErrors()) { notification.message() }
    }

    override fun validate(): Notification {
        val notification = Notification()

        if (text.isBlank()) {
            notification.addError("A phrase must not be blank!")
            return notification
        }
        if (groupingSignsAreNotCorrectlyClose())
            notification.addError("Parenthesis, brackets and/or braces should be closed appropriately!")
        if (textHasSymbolsWithinNotQuotedWords())
            notification.addError("Symbols should not be within not quoted words in a phrase. " +
                    "Provided: $text")

        return notification
    }

    private fun textHasSymbolsWithinNotQuotedWords() : Boolean {
        val notQuotedWords = Regex("\"[^\"]+\"|'[^']+'").split(text)

        val symbols = "[^a-z0-9(\\[{'\\- ]"
        val symbolsInside = "[a-z]+($symbols|[(\\[{])+[a-z]+"
        val startsWithSymbols = "$symbols+[a-z]+"
        val endsWithNotAllowedSymbols = "[a-z]+[^a-z0-9(\\[{'\\-,.;:?\\]})! ]"
        val pattern = Regex("$symbolsInside|$startsWithSymbols|$endsWithNotAllowedSymbols",
            RegexOption.IGNORE_CASE)

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

    override fun toString() = text
}

fun String.toPhrase() = Phrase(this)
