package br.all.application.review.find.services

import br.all.application.review.find.presenter.FindSystematicStudyPresenter
import br.all.application.review.find.services.FindSystematicStudyService.RequestModel
import br.all.application.review.find.services.FindSystematicStudyService.ResponseModel
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.shared.presenter.prepareIfUnauthorized
import br.all.application.user.CredentialsService

class FindSystematicStudyServiceImpl(
    private val repository: SystematicStudyRepository,
    private val credentialsService: CredentialsService,
) : FindSystematicStudyService {
    override fun findById(presenter: FindSystematicStudyPresenter, request: RequestModel) {
        val userId = request.userId

        val user = credentialsService.loadCredentials(userId)?.toUser()
        presenter.prepareIfUnauthorized(user)
        if (presenter.isDone()) return

        repository.findById(request.systematicStudy)?.let {
            presenter.prepareSuccessView(ResponseModel(userId, request.systematicStudy, it))
        }
    }
}
