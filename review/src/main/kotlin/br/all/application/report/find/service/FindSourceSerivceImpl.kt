package br.all.application.report.find.service

import br.all.application.report.find.presenter.FindSourcePresenter
import br.all.application.study.repository.StudyReviewRepository
import br.all.application.user.CredentialsService
import java.util.*

class FindSourceSerivceImpl(
    private val studyReviewRepository: StudyReviewRepository,
    private val credentialsService: CredentialsService,
): FindSourceService {
    override fun findSource(presenter: FindSourcePresenter, request: FindSourceService.RequestModel) {
        val response = FindSourceService.ResponseModel(
            userId = UUID.randomUUID(),
            systematicStudyId = UUID.randomUUID(),
            studyReviewId = 33333,
            source = "IEEE Xplore",
            included = listOf(11111, 22222),
            excluded = emptyList(),
            duplicated = listOf(33333),
            totalOfStudies = 3
        )

        presenter.prepareSuccessView(response)
    }
}