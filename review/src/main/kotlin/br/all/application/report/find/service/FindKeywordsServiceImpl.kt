package br.all.application.report.find.service

import br.all.application.protocol.repository.ProtocolRepository
import br.all.application.report.find.presenter.FindKeywordsPresenter
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.user.CredentialsService
import java.util.*

class FindKeywordsServiceImpl(
    private val protocolRepository: ProtocolRepository,
    private val systematicStudyRepository: SystematicStudyRepository,
    private val credentialsService: CredentialsService,
): FindKeywordsService {
    override fun findKeywords(presenter: FindKeywordsPresenter, request: FindKeywordsService.RequestModel) {
        val keys = listOf("keyword1", "keyword2", "keyword3", "keyword4")
        val response = FindKeywordsService.ResponseModel(
            UUID.randomUUID(),
            UUID.randomUUID(),
            filter = null,
            keys,
            keys.size,
        )
        presenter.prepareSuccessView(response)
    }
}