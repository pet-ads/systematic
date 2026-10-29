package br.all.application.study.majority

import br.all.application.reviewmode.repository.ReplicationDto
import br.all.application.reviewmode.repository.ReviewModeRepository
import br.all.application.reviewmode.repository.ReviewModeStageDto
import br.all.application.reviewmode.repository.ReviewerStudyRepository
import br.all.application.reviewmode.repository.SplitDto
import br.all.application.study.repository.StudyReviewDto
import br.all.application.study.repository.StudyReviewRepository
import br.all.application.study.repository.fromDto
import br.all.application.study.repository.toDto
import br.all.domain.model.study.StudyReview
import br.all.domain.model.study.StudyReviewStage
import java.util.UUID

class MajorityDecisionApplier(
    private val reviewModeRepository: ReviewModeRepository,
    private val reviewerStudyRepository: ReviewerStudyRepository,
    private val studyReviewRepository: StudyReviewRepository,
) {
    fun apply(systematicStudyId: UUID, studyReviewId: Long, stage: StudyReviewStage): Boolean {
        val reviewMode = reviewModeRepository.findById(systematicStudyId) ?: return false
        val stageMode = when (stage) {
            StudyReviewStage.SELECTION -> reviewMode.selection
            StudyReviewStage.EXTRACTION -> reviewMode.extraction
        }
        if (stageMode == null || !stageMode.acceptsMajority()) return false

        val copies = reviewerStudyRepository.findAllByStudy(systematicStudyId, studyReviewId, stage)
            .map { it.studyReview }
        if (copies.isEmpty()) return false

        val winner = listOf("INCLUDED", "EXCLUDED").firstOrNull { candidate ->
            copies.count { statusOf(it, stage) == candidate } * 2 > copies.size
        } ?: return false

        val originalDto = studyReviewRepository.findById(systematicStudyId, studyReviewId) ?: return false
        if (originalDto.selectionStatus == "DUPLICATED") return false
        if (statusOf(originalDto, stage) == winner) return false

        val original = StudyReview.fromDto(originalDto)
        try {
            when (stage) {
                StudyReviewStage.SELECTION ->
                    if (winner == "INCLUDED") original.includeInSelection() else original.excludeInSelection()
                StudyReviewStage.EXTRACTION ->
                    if (winner == "INCLUDED") original.includeInExtraction() else original.excludeInExtraction()
            }
        } catch (e: IllegalStateException) {
            return false
        }

        val agreeing = copies.filter { statusOf(it, stage) == winner }
        val updated = original.toDto().let { dto ->
            when (stage) {
                StudyReviewStage.SELECTION ->
                    dto.copy(selectionCriteria = dto.selectionCriteria + agreeing.flatMap { it.selectionCriteria })
                StudyReviewStage.EXTRACTION ->
                    dto.copy(extractionCriteria = dto.extractionCriteria + agreeing.flatMap { it.extractionCriteria })
            }
        }
        studyReviewRepository.saveOrUpdate(updated)
        return true
    }

    private fun statusOf(dto: StudyReviewDto, stage: StudyReviewStage) = when (stage) {
        StudyReviewStage.SELECTION -> dto.selectionStatus
        StudyReviewStage.EXTRACTION -> dto.extractionStatus
    }

    private fun ReviewModeStageDto.acceptsMajority() = when (this) {
        is SplitDto -> acceptMajority
        is ReplicationDto -> acceptMajority
    }
}