package br.all.application.report.find.service

import br.all.application.report.find.presenter.StudiesFunnelPresenter
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.review.repository.fromDto
import br.all.application.shared.presenter.prepareIfFailsPreconditions
import br.all.application.study.repository.StudyReviewDto
import br.all.application.study.repository.StudyReviewRepository
import br.all.application.user.CredentialsService
import br.all.domain.model.review.SystematicStudy
import br.all.domain.model.study.ExtractionStatus
import br.all.domain.model.study.SelectionStatus
import java.util.UUID

class StudiesFunnelServiceImpl(
    private val credentialsService: CredentialsService,
    private val studyReviewRepository: StudyReviewRepository,
    private val systematicStudyRepository: SystematicStudyRepository,
): StudiesFunnelService {
    override fun studiesFunnel(presenter: StudiesFunnelPresenter, request: StudiesFunnelService.RequestModel) {
        val user = credentialsService.loadCredentials(request.userId)?.toUser()

        val systematicStudyDto = systematicStudyRepository.findById(request.systematicStudyId)
        val systematicStudy = systematicStudyDto?.let { SystematicStudy.fromDto(it) }

        presenter.prepareIfFailsPreconditions(user, systematicStudy)
        if (presenter.isDone()) return

        val allStudies = studyReviewRepository.findAllFromReview(request.systematicStudyId)

        val response = createResponse(allStudies, request)

        presenter.prepareSuccessView(response)
    }

    private fun createResponse(allStudies: List<StudyReviewDto>, request: StudiesFunnelService.RequestModel): StudiesFunnelService.ResponseModel {
        // Total de estudos encontrados, fazer por source
        val totalIdentifiedBySource = allStudies
            .flatMap { it.searchSources }
            .groupingBy { it }
            .eachCount()

        // Remover os duplicados do total, e depois, fazer por source
        val nonDuplicatedStudies = allStudies.filter { it.selectionStatus != SelectionStatus.DUPLICATED.name }
        val totalAfterDuplicatesRemovedBySource = nonDuplicatedStudies
            .flatMap { it.searchSources }
            .groupingBy { it }
            .eachCount()

        // Total analisado na selection
        val totalScreened = allStudies.size

        // Total excluídos na selection
        val totalExcludedInScreening = allStudies.count { it.selectionStatus == SelectionStatus.EXCLUDED.name }

        // Dos excluídos na selection, fazer por criteria
        val excludedByCriterion = allStudies
            .filter { it.selectionStatus == SelectionStatus.EXCLUDED.name }
            .flatMap { it.criteria }
            .groupingBy { it }
            .eachCount()

        // Total analisados na extraction (passou da selection)
        val totalFullTextAssessed = allStudies.count { 
            it.selectionStatus == SelectionStatus.INCLUDED.name 
        }

        // Total excluídos na extraction
        val totalExcludedInFullText = allStudies.count { 
            it.selectionStatus == SelectionStatus.INCLUDED.name && 
            it.extractionStatus == ExtractionStatus.EXCLUDED.name 
        }

        // Dos excluídos na extraction, fazer por criteria
        val totalExcludedByCriterion = allStudies
            .filter { 
                it.selectionStatus == SelectionStatus.INCLUDED.name && 
                it.extractionStatus == ExtractionStatus.EXCLUDED.name 
            }
            .flatMap { it.criteria }
            .groupingBy { it }
            .eachCount()

        // Total incluído (passou na selection e extraction)
        val totalIncluded = allStudies.count { 
            it.selectionStatus == SelectionStatus.INCLUDED.name && 
            it.extractionStatus == ExtractionStatus.INCLUDED.name 
        }

        return StudiesFunnelService.ResponseModel(
            userId = request.userId,
            systematicStudyId = request.systematicStudyId,
            totalIdentifiedBySource = totalIdentifiedBySource,
            totalAfterDuplicatesRemovedBySource = totalAfterDuplicatesRemovedBySource,
            totalScreened = totalScreened,
            totalExcludedInScreening = totalExcludedInScreening,
            excludedByCriterion = excludedByCriterion,
            totalFullTextAssessed = totalFullTextAssessed,
            totalExcludedInFullText = totalExcludedInFullText,
            totalExcludedByCriterion = totalExcludedByCriterion,
            totalIncluded = totalIncluded
        )
    }
}
