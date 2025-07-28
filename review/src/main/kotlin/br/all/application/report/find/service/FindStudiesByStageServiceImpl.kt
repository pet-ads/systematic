package br.all.application.report.find.service

import br.all.application.collaboration.repository.CollaborationRepository
import br.all.application.collaboration.repository.toDomain
import br.all.application.report.find.presenter.FindStudiesByStagePresenter
import br.all.application.report.find.service.FindStudiesByStageService.StudyCollection
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
    private val collaborationRepository: CollaborationRepository,
): FindStudiesByStageService {
    override fun findStudiesByStage(presenter: FindStudiesByStagePresenter, request: FindStudiesByStageService.RequestModel) {
        val user = credentialsService.loadCredentials(request.userId)?.toUser()

        val systematicStudyDto = systematicStudyRepository.findById(request.systematicStudyId)
        val systematicStudy = systematicStudyDto?.let { SystematicStudy.fromDto(it) }
        val collaborations = collaborationRepository
            .listAllCollaborationsBySystematicStudyId(request.systematicStudyId)
            .map { it.toDomain() }

        presenter.prepareIfFailsPreconditions(user, systematicStudy, collaborations = collaborations)
        if (presenter.isDone()) return

        if (request.stage != "selection" && request.stage != "extraction") {
            presenter.prepareFailView(IllegalArgumentException(request.stage))
            if (presenter.isDone()) return
        }

        val allStudies = studyReviewRepository.findAllFromReview(request.systematicStudyId)

        val response = createResponse(allStudies, request)

        presenter.prepareSuccessView(response)
    }

    private fun createResponse(allStudies: List<StudyReviewDto>, request: FindStudiesByStageService.RequestModel): FindStudiesByStageService.ResponseModel {
        val includedStudiesIds = mutableListOf<Long>()
        val excludedStudiesIds = mutableListOf<Long>()
        val unclassifiedStudiesIds = mutableListOf<Long>()
        val duplicatedStudiesIds = mutableListOf<Long>()

        for (study in allStudies) {
            if (request.stage == "selection") {
                when (study.selectionStatus) {
                    SelectionStatus.INCLUDED.name -> includedStudiesIds.add(study.studyReviewId)
                    SelectionStatus.EXCLUDED.name -> excludedStudiesIds.add(study.studyReviewId)
                    SelectionStatus.UNCLASSIFIED.name -> unclassifiedStudiesIds.add(study.studyReviewId)
                    SelectionStatus.DUPLICATED.name -> duplicatedStudiesIds.add(study.studyReviewId)
                }
            }

            if (request.stage == "extraction") {
                when (study.extractionStatus) {
                    ExtractionStatus.INCLUDED.name -> includedStudiesIds.add(study.studyReviewId)
                    ExtractionStatus.EXCLUDED.name -> excludedStudiesIds.add(study.studyReviewId)
                    ExtractionStatus.UNCLASSIFIED.name -> unclassifiedStudiesIds.add(study.studyReviewId)
                    ExtractionStatus.DUPLICATED.name -> duplicatedStudiesIds.add(study.studyReviewId)
                }
            }
        }

        return FindStudiesByStageService.ResponseModel(
            userId = request.userId,
            systematicStudyId = request.systematicStudyId,
            stage = request.stage,
            included = StudyCollection(includedStudiesIds, includedStudiesIds.size),
            excluded = StudyCollection(excludedStudiesIds, excludedStudiesIds.size),
            unclassified = StudyCollection(unclassifiedStudiesIds, unclassifiedStudiesIds.size),
            duplicated = StudyCollection(duplicatedStudiesIds, duplicatedStudiesIds.size)
        )
    }
}