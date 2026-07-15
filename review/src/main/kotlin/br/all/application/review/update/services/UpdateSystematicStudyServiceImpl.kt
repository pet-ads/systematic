package br.all.application.review.update.services

import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.review.repository.toDto
import br.all.application.review.update.presenter.UpdateSystematicStudyPresenter
import br.all.application.review.update.services.UpdateSystematicStudyService.RequestModel
import br.all.application.review.update.services.UpdateSystematicStudyService.ResponseModel
import br.all.application.shared.service.AuthorizationService

class UpdateSystematicStudyServiceImpl(
    private val repository: SystematicStudyRepository,
    private val authorizationService: AuthorizationService,
) : UpdateSystematicStudyService {
    override fun update(presenter: UpdateSystematicStudyPresenter, request: RequestModel) {
        val context = authorizationService.authorize(
            presenter,
            request.userId,
            request.systematicStudy
        )

        if (presenter.isDone()){
            return
        }

        context!!

        val systematicStudy = context.systematicStudy
        val original = systematicStudy.toDto()

        val updated = systematicStudy.apply {
            title = request.title ?: title
            description = request.description ?: description
            objectives = request.objectives ?: objectives
        }.toDto()

        if (updated != original) repository.saveOrUpdate(updated)

        presenter.prepareSuccessView(ResponseModel(request.userId, request.systematicStudy))
    }
}
