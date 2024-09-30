package br.all.application.study.find.service

import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.review.repository.fromDto
import br.all.application.shared.presenter.prepareIfFailsPreconditions
import br.all.application.study.find.presenter.FindAllStudyReviewsBySessionPresenter
import br.all.application.study.repository.StudyReviewRepository
import br.all.application.user.CredentialsService
import br.all.domain.model.review.SystematicStudy

class FindAllStudyReviewsBySessionServiceImpl (
    private val systematicStudyRepository: SystematicStudyRepository,
    private val studyReviewRepository: StudyReviewRepository,
    private val credentialsService: CredentialsService,
) : FindAllStudyReviewsBySessionService {

    override fun findAllBySearchSession(
        presenter: FindAllStudyReviewsBySessionPresenter,
        request: FindAllStudyReviewsBySessionService.RequestModel
    ) {
        val user = credentialsService.loadCredentials(request.userId)?.toUser()

        val systematicStudyDto = systematicStudyRepository.findById(request.systematicStudyId)
        val systematicStudy = systematicStudyDto?.let { SystematicStudy.fromDto(it) }

        presenter.prepareIfFailsPreconditions(user, systematicStudy)

        if (presenter.isDone()) return

        val studyReviews = studyReviewRepository.findAllBySession(request.systematicStudyId, request.searchSessionId)
        presenter.prepareSuccessView(
            FindAllStudyReviewsBySessionService.ResponseModel(
                request.userId, request.systematicStudyId,
                request.systematicStudyId, studyReviews
            )
        )
    }
}