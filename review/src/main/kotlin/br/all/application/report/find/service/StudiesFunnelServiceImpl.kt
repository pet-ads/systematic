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
            totalIdentifiedBySource = mapOf(
                "PubMed" to 6,
                "IEEE Xplore" to 4
            ),
            totalAfterDuplicatesRemovedBySource = mapOf(
                "PubMed" to 5,
                "IEEE Xplore" to 3
            ),
            totalScreened = 8,
            totalExcludedInScreening = 4,
            excludedByCriterion = mapOf(
                "Título irrelevante" to 2,
                "Resumo inadequado" to 2
            ),
            totalFullTextAssessed = 4,
            totalExcludedInFullText = 2,
            totalExcludedByCriterion = mapOf(
                "Critério de qualidade" to 1,
                "Escopo incorreto" to 1
            ),
            totalIncluded = 2
        )


        presenter.prepareSuccessView(response)
    }
}