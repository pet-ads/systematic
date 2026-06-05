package br.all.application.report.find.service

import br.all.application.report.find.presenter.StudiesFunnelPresenter
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.review.repository.fromDto
import br.all.application.shared.presenter.FunnelCalculator
import br.all.application.shared.presenter.prepareIfFailsPreconditions
import br.all.application.study.repository.StudyReviewDto
import br.all.application.study.repository.StudyReviewRepository
import br.all.application.user.CredentialsService
import br.all.domain.model.review.SystematicStudy
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

    private fun createResponse(
        allStudies: List<StudyReviewDto>,
        request: StudiesFunnelService.RequestModel
    ): StudiesFunnelService.ResponseModel {
        val funnelData = FunnelCalculator.calculate(allStudies)

        val response = StudiesFunnelService.ResponseModel(
            userId = request.userId,
            systematicStudyId = request.systematicStudyId,
            totalIdentifiedBySource = funnelData.totalIdentifiedBySource,
            totalAfterDuplicatesRemovedBySource = funnelData.totalAfterDuplicatesRemovedBySource,
            totalScreened = funnelData.totalScreened,
            totalExcludedInScreening = funnelData.totalExcludedInScreening,
            excludedByCriterion = funnelData.excludedByCriterion,
            totalFullTextAssessed = funnelData.totalFullTextAssessed,
            totalExcludedInFullText = funnelData.totalExcludedInFullText,
            totalExcludedByCriterion = funnelData.totalExcludedByCriterion,
            totalIncluded = funnelData.totalIncluded
        )
        return response

    }
}
