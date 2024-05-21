package br.all.application.review.update.services

import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.review.repository.fromDto
import br.all.application.review.repository.toDto
import br.all.application.review.update.presenter.UpdateSystematicStudyPresenter
import br.all.application.review.update.services.UpdateSystematicStudyService.RequestModel
import br.all.application.review.update.services.UpdateSystematicStudyService.ResponseModel
import br.all.application.shared.exceptions.EntityNotFoundException
import br.all.application.shared.presenter.prepareIfUnauthorized
import br.all.application.user.CredentialsService
import br.all.domain.model.review.SystematicStudy

class UpdateSystematicStudyServiceImpl(
    private val repository: SystematicStudyRepository,
    private val credentialsService: CredentialsService,
) : UpdateSystematicStudyService {
    override fun update(presenter: UpdateSystematicStudyPresenter, request: RequestModel) {
        val userId = request.userId

        val user = credentialsService.loadCredentials(userId)?.toUser()
        presenter.prepareIfUnauthorized(user)
        if (presenter.isDone()) return

        val systematicStudy = request.systematicStudy

        val dto = repository.findById(systematicStudy)
        if(dto == null) {
            val message =
                "There is no systematic study of id ${request.systematicStudy} or systematic study of id ${request.systematicStudy}."
            presenter.prepareFailView(EntityNotFoundException(message))
            return
        }
        val updated = SystematicStudy.fromDto(dto).apply {
            title = request.title ?: title
            description = request.description ?: description
        }.toDto()

        if (updated != dto) repository.saveOrUpdate(updated)
        presenter.prepareSuccessView(ResponseModel(userId, systematicStudy))
    }
}
