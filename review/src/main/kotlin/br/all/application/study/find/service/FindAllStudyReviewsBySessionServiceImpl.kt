package br.all.application.study.find.service

import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.review.repository.fromDto
import br.all.application.shared.presenter.prepareIfFailsPreconditions
import br.all.application.study.find.presenter.FindAllStudyReviewsBySessionPresenter
import br.all.application.study.repository.StudyReviewRepository
import br.all.application.user.CredentialsService
import br.all.domain.model.review.SystematicStudy
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort

class FindAllStudyReviewsBySessionServiceImpl (
    private val systematicStudyRepository: SystematicStudyRepository,
    private val studyReviewRepository: StudyReviewRepository,
    private val credentialsService: CredentialsService,
) : FindAllStudyReviewsBySessionService {

    private fun parseSortParameter(sortParam: String): Sort {
        val parts = sortParam.split(",")
        val property = parts[0]
        val direction = if (parts.size > 1 && parts[1].equals("desc", ignoreCase = true)) {
            Sort.Direction.DESC
        } else {
            Sort.Direction.ASC
        }
        return Sort.by(direction, property)
    }

    override fun findAllBySearchSession(
        presenter: FindAllStudyReviewsBySessionPresenter,
        request: FindAllStudyReviewsBySessionService.RequestModel
    ) {
        val user = credentialsService.loadCredentials(request.userId)?.toUser()

        val systematicStudyDto = systematicStudyRepository.findById(request.systematicStudyId)
        val systematicStudy = systematicStudyDto?.let { SystematicStudy.fromDto(it) }

        presenter.prepareIfFailsPreconditions(user, systematicStudy)

        if (presenter.isDone()) return

        val sort = parseSortParameter(request.sort)
        val pageable = PageRequest.of(
            request.page,
            request.pageSize,
            sort
        )

        val studyReviewsPage = studyReviewRepository.findAllBySessionPaged(
            request.systematicStudyId,
            request.searchSessionId,
            pageable
        )

        presenter.prepareSuccessView(
            FindAllStudyReviewsBySessionService.ResponseModel(
                userId = request.userId,
                systematicStudyId = request.systematicStudyId,
                searchSessionId = request.searchSessionId,
                studyReviews = studyReviewsPage.content,
                page = pageable.pageNumber,
                size = pageable.pageSize,
                totalElements = studyReviewsPage.totalElements,
                totalPages = studyReviewsPage.totalPages
            )
        )
    }
}