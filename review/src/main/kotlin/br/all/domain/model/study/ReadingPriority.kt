package br.all.domain.model.study

enum class ReadingPriority {
    VERY_LOW,
    LOW,
    HIGH,
    VERY_HIGH

    companion object {
        fun convertStringToReadingPriorityEnum(value: String): ReadingPriority {
            return when(value){
                "VERY_LOW" -> VERY_LOW
                "LOW" -> LOW
                "HIGH" -> HIGH
                "VERY_HIGH" -> VERY_HIGH
                else -> {LOW}
            }
        }
    }
}