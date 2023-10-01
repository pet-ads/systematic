package br.all.domain.model.study

enum class ExtractionStatus {
    UNCLASSIFIED,
    DUPLICATED,
    INCLUDED,
    EXCLUDED;

    companion object {
        fun convertStringToExtractionStatusEnum(value: String): ExtractionStatus {
            return when(value){
                "UNCLASSIFIED" -> ExtractionStatus.UNCLASSIFIED
                "DUPLICATED" -> ExtractionStatus.DUPLICATED
                "INCLUDED" -> ExtractionStatus.INCLUDED
                "EXCLUDED" -> ExtractionStatus.EXCLUDED
                else -> {
                    ExtractionStatus.UNCLASSIFIED
                }
            }
        }
    }
}