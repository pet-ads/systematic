package br.all.application.report.export

data class FunnelExportData(
    val totalIdentifiedBySource: Map<String, Int>,
    val totalAfterDuplicatesRemovedBySource: Map<String, Int>,
    val totalScreened: Int,
    val totalExcludedInScreening: Int,
    val excludedByCriterion: Map<String, Int>,
    val totalFullTextAssessed: Int,
    val totalExcludedInFullText: Int,
    val totalExcludedByCriterion: Map<String, Int>,
    val totalIncluded: Int
)


