package br.all.application.report.find.service

import br.all.application.report.find.presenter.StudiesFunnelPresenter
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.study.repository.StudyReviewRepository
import br.all.application.user.CredentialsService
import java.util.UUID

class StudiesFunnelServiceImpl(
    private val credentialsService: CredentialsService,
    private val studyReviewRepository: StudyReviewRepository,
    private val systematicStudyRepository: SystematicStudyRepository,
): StudiesFunnelService {
    override fun studiesFunnel(presenter: StudiesFunnelPresenter, request: StudiesFunnelService.RequestModel) {
        val response = StudiesFunnelService.ResponseModel(
            userId = UUID.randomUUID(),
            systematicStudyId = UUID.randomUUID(),
            totalStudies = 10,
            totalAfterDuplicates = 8,
            totalOfExcludedStudies = 6,
            totalExcludedInExtraction = 1,
            totalIncluded = 1
        )

        presenter.prepareSuccessView(response)
    }
}