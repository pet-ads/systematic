package br.all.application.study.find.service

import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.review.repository.fromDto
import br.all.application.shared.presenter.prepareIfFailsPreconditions
import br.all.application.study.find.presenter.FindAllStudyReviewsBySourcePresenter
import br.all.application.study.find.service.FindAllStudyReviewsBySourceService.RequestModel
import br.all.application.study.find.service.FindAllStudyReviewsBySourceService.ResponseModel
import br.all.application.study.repository.StudyReviewRepository
import br.all.application.user.CredentialsService
import br.all.domain.model.protocol.SearchSource
import br.all.domain.model.review.SystematicStudy

class FindAllStudyReviewsBySourceServiceImpl(
    private val systematicStudyRepository: SystematicStudyRepository,
    private val studyReviewRepository: StudyReviewRepository,
    private val credentialsService: CredentialsService,
) : FindAllStudyReviewsBySourceService {

    override fun findAllFromSearchSession(presenter: FindAllStudyReviewsBySourcePresenter, request: RequestModel) {
        val user = credentialsService.loadCredentials(request.userId)?.toUser()

        val systematicStudyDto = systematicStudyRepository.findById(request.systematicStudyId)
        val systematicStudy = systematicStudyDto?.let { SystematicStudy.fromDto(it) }

        presenter.prepareIfFailsPreconditions(user, systematicStudy)

        val searchSource = SearchSource(request.searchSource)

        if (presenter.isDone()) return

        val studyReviews = studyReviewRepository.findAllBySource(request.systematicStudyId, searchSource.toString())
        presenter.prepareSuccessView(
            ResponseModel(
                request.userId, request.systematicStudyId,
                request.searchSource, studyReviews
            )
        )
    }
}

