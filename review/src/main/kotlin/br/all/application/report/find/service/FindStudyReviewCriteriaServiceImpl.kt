package br.all.application.report.find.service

import br.all.application.protocol.repository.CriterionDto
import br.all.application.protocol.repository.ProtocolRepository
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.review.repository.fromDto
import br.all.application.shared.presenter.prepareIfFailsPreconditions
import br.all.application.report.find.presenter.FindStudyReviewCriteriaPresenter
import br.all.application.study.repository.StudyReviewDto
import br.all.application.study.repository.StudyReviewRepository
import br.all.application.user.CredentialsService
import br.all.domain.model.review.SystematicStudy

class FindStudyReviewCriteriaServiceImpl(
    private val systematicStudyRepository: SystematicStudyRepository,
    private val studyReviewRepository: StudyReviewRepository,
    private val credentialsService: CredentialsService,
    private val protocolRepository: ProtocolRepository,
): FindStudyReviewCriteriaService {
    override fun findCriteria(
        presenter: FindStudyReviewCriteriaPresenter,
        request: FindStudyReviewCriteriaService.RequestModel
    ) {
        val user = credentialsService.loadCredentials(request.userId)?.toUser()

        val systematicStudyDto = systematicStudyRepository.findById(request.systematicStudyId)
        val systematicStudy = systematicStudyDto?.let { SystematicStudy.fromDto(it) }

        presenter.prepareIfFailsPreconditions(user, systematicStudy)

        if(presenter.isDone()) return

        val criteriaSet = protocolRepository.findById(request.systematicStudyId)?.eligibilityCriteria?: emptyList()

        val studyReview = studyReviewRepository.findById(request.systematicStudyId, request.studyReviewId)!!

        val inclusionCriteria = getCriteriaByType(criteriaSet, studyReview, "INCLUSION")
        val exclusionCriteria = getCriteriaByType(criteriaSet, studyReview, "EXCLUSION")

        val response = FindStudyReviewCriteriaService.ResponseModel(
            userId = request.userId,
            systematicStudyId = request.systematicStudyId,
            studyReviewId = request.studyReviewId,
            inclusionCriteria = inclusionCriteria,
            exclusionCriteria = exclusionCriteria,
        )

        presenter.prepareSuccessView(response)
    }

    private fun getCriteriaByType(
        criteriaSet: Collection<CriterionDto>,
        studyReview: StudyReviewDto,
        type: String
    ): Set<String> {
        return criteriaSet
            .filter { it.type == type && it.description in studyReview.criteria }
            .map { it.description }.toSet()
    }
}