package br.all.application.shared.presenter

object AnswerAggregator {
    private val LABEL_REGEX = Regex("""Label\(name:\s*(.+?)[,;]\s*value:\s*(\d+)\)""")

    fun formatAnswerLabel(raw: String): String {
        val match = LABEL_REGEX.find(raw) ?: return raw
        val (name, value) = match.destructured
        return "$value - $name"
    }

    fun parsePickManyLabel(label: String): List<String> {
        val trimmed = label.trim()
        return if (trimmed.startsWith("[") && trimmed.endsWith("]")) {
            trimmed.substring(1, trimmed.length - 1)
                .split(",")
                .map { it.trim() }
                .filter { it.isNotEmpty() }
        } else listOf(trimmed)
    }
}