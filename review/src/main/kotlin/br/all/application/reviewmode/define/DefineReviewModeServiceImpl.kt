package br.all.application.reviewmode.define

import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.review.repository.fromDto
import br.all.application.reviewmode.define.DefineReviewModeService.RequestModel
import br.all.application.reviewmode.define.DefineReviewModeService.ResponseModel
import br.all.application.reviewmode.repository.ReviewModeRepository
import br.all.application.reviewmode.repository.toDomain
import br.all.application.shared.presenter.prepareIfFailsPreconditions
import br.all.application.user.CredentialsService
import br.all.domain.model.review.SystematicStudy
import org.springframework.stereotype.Service

@Service
class DefineReviewModeServiceImpl(
    private val systematicStudyRepository: SystematicStudyRepository,
    private val reviewModeRepository: ReviewModeRepository,
    private val credentialsService: CredentialsService,
) : DefineReviewModeService {

    override fun define(presenter: DefineReviewModePresenter, request: RequestModel) {
        val user = credentialsService.loadCredentials(request.userId)?.toUser()
        val systematicStudyDto = systematicStudyRepository.findById(request.systematicStudyId)
        val systematicStudy = systematicStudyDto?.let { SystematicStudy.fromDto(it) }

        presenter.prepareIfFailsPreconditions(user, systematicStudy)
        if (presenter.isDone()) return

        val reviewModeDto = reviewModeRepository.findById(request.systematicStudyId)
        val reviewMode = reviewModeDto?.toDomain()

        reviewMode?.defineMode(request.stage, request.toReviewMode())

        presenter.prepareSuccessView(
            ResponseModel(request.userId, request.systematicStudyId, request.stage)
        )
    }
}
