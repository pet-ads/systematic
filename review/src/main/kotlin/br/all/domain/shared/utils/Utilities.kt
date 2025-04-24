package br.all.domain.shared.utils

import java.text.Normalizer

inline fun exists(exist: Boolean, message: () -> String = { "There is no such element!" }) {
    if (!exist) throw NoSuchElementException(message())
}

fun normalizeText(text: String): String {
    return Normalizer.normalize(text.lowercase(), Normalizer.Form.NFD)
        .replace(Regex("\\p{InCombiningDiacriticalMarks}+"), "")
        .replace(Regex("[^\\p{Alnum}\\s]"), "")
        .replace(Regex("\\s+"), " ")
        .trim()
}