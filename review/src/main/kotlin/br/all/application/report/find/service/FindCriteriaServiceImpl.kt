package br.all.application.report.find.service

import br.all.application.protocol.repository.CriterionDto
import br.all.application.protocol.repository.ProtocolRepository
import br.all.application.report.find.presenter.FindCriteriaPresenter
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.review.repository.fromDto
import br.all.application.shared.presenter.prepareIfFailsPreconditions
import br.all.application.study.repository.StudyReviewRepository
import br.all.application.user.CredentialsService
import br.all.domain.model.review.SystematicStudy
import br.all.domain.model.study.StudyReviewStage

class FindCriteriaServiceImpl(
    private val protocolRepository: ProtocolRepository,
    private val systematicStudyRepository: SystematicStudyRepository,
    private val credentialsService: CredentialsService,
    private val studyReviewRepository: StudyReviewRepository,
    ): FindCriteriaService {
    override fun findCriteria(presenter: FindCriteriaPresenter, request: FindCriteriaService.RequestModel) {
        val user = credentialsService.loadCredentials(request.userId)?.toUser()

        val systematicStudyDto = systematicStudyRepository.findById(request.systematicStudyId)
        val systematicStudy = systematicStudyDto?.let { SystematicStudy.fromDto(it) }

        presenter.prepareIfFailsPreconditions(user, systematicStudy)

        if(presenter.isDone()) return

        val criteriaSet = protocolRepository.findById(request.systematicStudyId)?.eligibilityCriteria
            ?.filter { it.type == request.type }
            ?: emptyList()

        val studyReviewList = studyReviewRepository.findAllFromReview(request.systematicStudyId)

        val result = criteriaSet.associateWith { criterion ->
            studyReviewList.filter { review ->
                val criteria = if (request.stage == StudyReviewStage.SELECTION)
                    review.selectionCriteria
                else
                    review.extractionCriteria

                criterion.description in criteria
            }.map { it.studyReviewId }
        }

        val filteredCriteria = FindCriteriaService.FoundStudies(
            included = result
        )

        val response = FindCriteriaService.ResponseModel(
            userId = request.userId,
            systematicStudyId = request.systematicStudyId,
            stage = request.stage,
            criteria = filteredCriteria,
        )

        presenter.prepareSuccessView(response)
    }
}