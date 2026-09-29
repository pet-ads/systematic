package br.all.application.shared.presenter

import br.all.application.protocol.repository.ProtocolRepository
import br.all.application.report.export.GroupedTableRow
import br.all.application.study.repository.StudyReviewDto
import br.all.domain.model.study.StudyReviewStage
import java.util.UUID

object CriteriaGrouping {
    fun groupByCriterion(
        protocolRepository: ProtocolRepository,
        systematicStudyId: UUID,
        type: String, // "INCLUSION" ou "EXCLUSION"
        stage: StudyReviewStage,
        studies: List<StudyReviewDto>
    ): List<GroupedTableRow> {
        val criteria = protocolRepository.findById(systematicStudyId)?.eligibilityCriteria
            ?.filter { it.type == type }
            ?: emptyList()
        val total = studies.size

        val criteriaOf: (StudyReviewDto) -> Set<String> =
            if (stage == StudyReviewStage.SELECTION) { s -> s.selectionCriteria }
            else { s -> s.extractionCriteria }

        return criteria.map { criterion ->
            val ids = studies.filter { criterion.description in criteriaOf(it) }.map { it.studyReviewId }
            GroupedTableRow(
                criterion = criterion.description,
                studyIds = ids,
                count = ids.size,
                percentage = if (total > 0) ids.size * 100.0 / total else 0.0
            )
        }
    }
}