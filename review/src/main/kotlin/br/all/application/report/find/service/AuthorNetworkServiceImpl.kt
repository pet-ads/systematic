package br.all.application.report.find.service

import br.all.application.report.find.presenter.AuthorNetworkPresenter
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.study.repository.StudyReviewRepository
import br.all.application.user.CredentialsService
import java.util.*

class AuthorNetworkServiceImpl(
    private val credentialsService: CredentialsService,
    private val studyReviewRepository: StudyReviewRepository,
    private val systematicStudyRepository: SystematicStudyRepository,
): AuthorNetworkService {
    override fun findAuthors(presenter: AuthorNetworkPresenter, request: AuthorNetworkService.RequestModel) {
        val response = AuthorNetworkService.ResponseModel(
            userId = UUID.randomUUID(),
            systematicStudyId = UUID.randomUUID(),
            studyReviewId = 44444,
            nodes = listOf(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID()),
            edges = listOf(11111, 22222, 33333, 55555)
        )

        presenter.prepareSuccessView(response)
    }
}

