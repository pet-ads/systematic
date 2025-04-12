package br.all.application.report.find.service

import br.all.application.protocol.repository.ProtocolRepository
import br.all.application.protocol.repository.toDto
import br.all.application.report.find.presenter.FindCriteriaPresenter
import br.all.application.study.repository.StudyReviewRepository
import br.all.application.user.CredentialsService
import br.all.domain.model.protocol.Criterion
import java.util.*

class FindCriteriaServiceImpl(
    private val protocolRepository: ProtocolRepository,
    private val credentialsService: CredentialsService,
    private val studyReviewRepository: StudyReviewRepository,
    ): FindCriteriaService {
    override fun findCriteria(presenter: FindCriteriaPresenter, request: FindCriteriaService.RequestModel) {

        val criteria = Criterion(
            type = Criterion.CriterionType.INCLUSION,
            description = "blablabla"
        )

        val criteriaDto = criteria.toDto()

        val response = FindCriteriaService.ResponseModel(
            userId = UUID.randomUUID(),
            systematicStudyId = UUID.randomUUID(),
            studyReviewId = 22222,
            criteria = criteriaDto,
        )

        presenter.prepareSuccessView(response)
    }
}