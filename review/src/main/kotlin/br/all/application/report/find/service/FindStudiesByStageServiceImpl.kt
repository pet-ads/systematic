package br.all.application.report.find.service

import br.all.application.report.find.presenter.FindStudiesByStagePresenter
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.study.repository.StudyReviewRepository
import br.all.application.user.CredentialsService
import java.util.*

class FindStudiesByStageServiceImpl(
    private val credentialsService: CredentialsService,
    private val studyReviewRepository: StudyReviewRepository,
    private val systematicStudyRepository: SystematicStudyRepository,
): FindStudiesByStageService {
    override fun findStudiesByStage(presenter: FindStudiesByStagePresenter, request: FindStudiesByStageService.RequestModel) {
        val response = FindStudiesByStageService.ResponseModel(
            userId = UUID.randomUUID(),
            systematicStudyId = UUID.randomUUID(),
            includedStudies = listOf(2222, 33333),
            excludedStudies = listOf(77777),
            unclassifiedStudies = listOf(44444),
            duplicatedStudies = listOf(55555),
            totalAmount = 5,
            stage = request.stage,
        )

        presenter.prepareSuccessView(response)
    }
}