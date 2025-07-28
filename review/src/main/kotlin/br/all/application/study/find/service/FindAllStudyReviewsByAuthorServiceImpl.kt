package br.all.application.study.find.service

import br.all.application.collaboration.repository.CollaborationRepository
import br.all.application.collaboration.repository.toDomain
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.review.repository.fromDto
import br.all.application.shared.presenter.prepareIfFailsPreconditions
import br.all.application.study.find.presenter.FindAllStudyReviewsByAuthorPresenter
import br.all.application.study.find.service.FindAllStudyReviewsByAuthorService.ResponseModel
import br.all.application.study.repository.StudyReviewRepository
import br.all.application.user.CredentialsService
import br.all.domain.model.review.SystematicStudy

class FindAllStudyReviewsByAuthorServiceImpl (
    private val systematicStudyRepository: SystematicStudyRepository,
    private val studyReviewRepository: StudyReviewRepository,
    private val credentialsService: CredentialsService,
    private val collaborationRepository: CollaborationRepository,
) : FindAllStudyReviewsByAuthorService {
    override fun findAllByAuthor(
        presenter: FindAllStudyReviewsByAuthorPresenter,
        request: FindAllStudyReviewsByAuthorService.RequestModel
    ) {
        val user = credentialsService.loadCredentials(request.userId)?.toUser()

        val systematicStudyDto = systematicStudyRepository.findById(request.systematicStudyId)
        val systematicStudy = systematicStudyDto?.let { SystematicStudy.fromDto(it) }
        val collaborations = collaborationRepository
            .listAllCollaborationsBySystematicStudyId(request.systematicStudyId)
            .map { it.toDomain() }

        presenter.prepareIfFailsPreconditions(user, systematicStudy, collaborations = collaborations)
        if (presenter.isDone()) return

        val allStudyReviews = studyReviewRepository.findAllFromReview(request.systematicStudyId)

        val filteredStudies = allStudyReviews.filter { review ->
            review.authors.contains(request.author, ignoreCase = true)
        }

        if (filteredStudies.isEmpty()) {
            val message = "No systematic studies were found for author: ${request.author}"
            presenter.prepareFailView(NoSuchElementException(message))
            return
        }

        presenter.prepareSuccessView(
            ResponseModel(
                userId = request.userId,
                systematicStudyId = request.systematicStudyId,
                studyReviews = filteredStudies
            )
        )
    }
}