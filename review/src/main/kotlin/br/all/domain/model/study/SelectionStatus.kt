package br.all.domain.model.study

enum class SelectionStatus {
    UNCLASSIFIED,
    DUPLICATED,
    INCLUDED,
    EXCLUDED;

    companion object {
        fun convertStringToSelectionStatusEnum(value: String): SelectionStatus {
            return when(value){
                "UNCLASSIFIED" -> SelectionStatus.UNCLASSIFIED
                "DUPLICATED" -> SelectionStatus.DUPLICATED
                "INCLUDED" -> SelectionStatus.INCLUDED
                "EXCLUDED" -> SelectionStatus.EXCLUDED
                else -> {
                    SelectionStatus.UNCLASSIFIED
                }
            }
        }
    }
}