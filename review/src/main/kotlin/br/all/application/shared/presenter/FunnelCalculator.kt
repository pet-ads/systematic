package br.all.application.shared.presenter

import br.all.application.report.export.FunnelExportData
import br.all.application.study.repository.StudyReviewDto
import br.all.domain.model.study.ExtractionStatus
import br.all.domain.model.study.SelectionStatus

object FunnelCalculator {
    fun calculate(studies: List<StudyReviewDto>): FunnelExportData {
        val nonDuplicated = studies.filter { it.selectionStatus != SelectionStatus.DUPLICATED.name }
        return FunnelExportData(
            totalIdentifiedBySource = studies.flatMap { it.searchSources }.groupingBy { it }.eachCount(),
            totalAfterDuplicatesRemovedBySource = nonDuplicated.flatMap { it.searchSources }.groupingBy { it }.eachCount(),
            totalScreened = studies.size,
            totalExcludedInScreening = studies.count { it.selectionStatus == SelectionStatus.EXCLUDED.name },
            excludedByCriterion = studies.filter { it.selectionStatus == SelectionStatus.EXCLUDED.name }
                .flatMap { it.criteria }.groupingBy { it }.eachCount(),
            totalFullTextAssessed = studies.count { it.selectionStatus == SelectionStatus.INCLUDED.name },
            totalExcludedInFullText = studies.count {
                it.selectionStatus == SelectionStatus.INCLUDED.name &&
                        it.extractionStatus == ExtractionStatus.EXCLUDED.name
            },
            totalExcludedByCriterion = studies.filter {
                it.selectionStatus == SelectionStatus.INCLUDED.name &&
                        it.extractionStatus == ExtractionStatus.EXCLUDED.name
            }.flatMap { it.criteria }.groupingBy { it }.eachCount(),
            totalIncluded = studies.count {
                it.selectionStatus == SelectionStatus.INCLUDED.name &&
                        it.extractionStatus == ExtractionStatus.INCLUDED.name
            }
        )
    }
}
