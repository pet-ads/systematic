package br.all.application.reviewmode.assign

import br.all.application.reviewmode.repository.ReviewModeRepository
import br.all.application.shared.service.AuthorizationService
import br.all.domain.shared.exception.EntityNotFoundException
import br.all.domain.shared.user.Role
import org.springframework.stereotype.Service

@Service
class AssignStudiesServiceImpl(
    private val reviewModeRepository: ReviewModeRepository,
    private val authorizationService: AuthorizationService,
) : AssignStudiesService {

    override fun assign(presenter: AssignStudiesPresenter, request: AssignStudiesService.RequestModel) {
        if (authorizationService.authorize(
                presenter,
                request.userId,
                request.systematicStudyId,
                setOf(Role.OWNER)
            ) == null
        ) return

        val reviewMode = reviewModeRepository.findById(request.systematicStudyId)
        if (reviewMode == null) {
            presenter.prepareFailView(
                EntityNotFoundException("Review mode of systematic study ${request.systematicStudyId} not found")
            )
            return
        }
    }
}