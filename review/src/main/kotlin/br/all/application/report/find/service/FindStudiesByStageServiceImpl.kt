package br.all.application.report.find.service

import br.all.application.report.find.presenter.FindStudiesByStagePresenter
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.review.repository.fromDto
import br.all.application.shared.presenter.prepareIfFailsPreconditions
import br.all.application.study.repository.StudyReviewDto
import br.all.application.study.repository.StudyReviewRepository
import br.all.application.user.CredentialsService
import br.all.domain.model.review.SystematicStudy
import br.all.domain.model.study.ExtractionStatus
import br.all.domain.model.study.SelectionStatus

class FindStudiesByStageServiceImpl(
    private val credentialsService: CredentialsService,
    private val studyReviewRepository: StudyReviewRepository,
    private val systematicStudyRepository: SystematicStudyRepository,
): FindStudiesByStageService {
    override fun findStudiesByStage(presenter: FindStudiesByStagePresenter, request: FindStudiesByStageService.RequestModel) {
        val user = credentialsService.loadCredentials(request.userId)?.toUser()

        val systematicStudyDto = systematicStudyRepository.findById(request.systematicStudyId)
        val systematicStudy = systematicStudyDto?.let { SystematicStudy.fromDto(it) }

        presenter.prepareIfFailsPreconditions(user, systematicStudy)
        if (presenter.isDone()) return

        if (request.stage != "selection" && request.stage != "extraction") {
            presenter.prepareFailView(IllegalArgumentException(request.stage))
            if (presenter.isDone()) return
        }

        val allStudies = studyReviewRepository.findAllFromReview(request.systematicStudyId)

        val response = when (request.stage) {
            "selection" -> selectionStageResponse(allStudies, request)
            "extraction" -> extractionStageResponse(allStudies, request)
            else -> throw IllegalArgumentException(request.stage)
        }

        presenter.prepareSuccessView(response)
    }

    private fun selectionStageResponse(allStudies: List<StudyReviewDto>, request: FindStudiesByStageService.RequestModel): FindStudiesByStageService.ResponseModel {
        val includedStudies = mutableListOf<Long>()
        val excludedStudies = mutableListOf<Long>()
        val unclassifiedStudies = mutableListOf<Long>()
        val duplicatedStudies = mutableListOf<Long>()

        for (study in allStudies) {
            when (study.selectionStatus) {
                SelectionStatus.INCLUDED.name -> includedStudies.add(study.studyReviewId)
                SelectionStatus.EXCLUDED.name -> excludedStudies.add(study.studyReviewId)
                SelectionStatus.UNCLASSIFIED.name -> unclassifiedStudies.add(study.studyReviewId)
                SelectionStatus.DUPLICATED.name -> duplicatedStudies.add(study.studyReviewId)
            }
        }

        val totalAmount = includedStudies.size + excludedStudies.size + unclassifiedStudies.size + duplicatedStudies.size

        return FindStudiesByStageService.ResponseModel(
            userId = request.userId,
            systematicStudyId = request.systematicStudyId,
            stage = request.stage,
            includedStudies = includedStudies,
            excludedStudies = excludedStudies,
            unclassifiedStudies = unclassifiedStudies,
            duplicatedStudies = duplicatedStudies,
            totalAmount = totalAmount,
        )
    }

    private fun extractionStageResponse(allStudies: List<StudyReviewDto>, request: FindStudiesByStageService.RequestModel): FindStudiesByStageService.ResponseModel {
        val includedStudies = mutableListOf<Long>()
        val excludedStudies = mutableListOf<Long>()
        val unclassifiedStudies = mutableListOf<Long>()
        val duplicatedStudies = mutableListOf<Long>()

        for (study in allStudies) {
            when (study.extractionStatus) {
                ExtractionStatus.INCLUDED.name -> includedStudies.add(study.studyReviewId)
                ExtractionStatus.EXCLUDED.name -> excludedStudies.add(study.studyReviewId)
                ExtractionStatus.UNCLASSIFIED.name -> unclassifiedStudies.add(study.studyReviewId)
                ExtractionStatus.DUPLICATED.name -> duplicatedStudies.add(study.studyReviewId)
            }
        }

        val totalAmount = includedStudies.size + excludedStudies.size + unclassifiedStudies.size + duplicatedStudies.size

        return FindStudiesByStageService.ResponseModel(
            userId = request.userId,
            systematicStudyId = request.systematicStudyId,
            stage = request.stage,
            includedStudies = includedStudies,
            excludedStudies = excludedStudies,
            unclassifiedStudies = unclassifiedStudies,
            duplicatedStudies = duplicatedStudies,
            totalAmount = totalAmount,
        )
    }
}