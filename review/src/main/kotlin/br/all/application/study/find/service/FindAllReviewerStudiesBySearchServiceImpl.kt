package br.all.application.study.find.service

import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.review.repository.fromDto
import br.all.application.reviewmode.repository.ReviewerStudySearchRepository
import br.all.application.shared.presenter.prepareIfFailsPreconditions
import br.all.application.study.find.presenter.FindAllStudyReviewsByAdvancedSearchPresenter
import br.all.application.study.find.service.FindAllReviewerStudiesBySearchService.RequestModel
import br.all.application.user.CredentialsService
import br.all.domain.model.review.SystematicStudy
import br.all.domain.shared.exception.UnauthorizedUserException
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort

class FindAllReviewerStudiesBySearchServiceImpl(
    private val systematicStudyRepository: SystematicStudyRepository,
    private val reviewerStudySearchRepository: ReviewerStudySearchRepository,
    private val credentialsService: CredentialsService,
) : FindAllReviewerStudiesBySearchService {

    override fun findAllBySearch(presenter: FindAllStudyReviewsByAdvancedSearchPresenter, request: RequestModel) {
        val user = credentialsService.loadCredentials(request.userId)?.toUser()
        val systematicStudyDto = systematicStudyRepository.findById(request.systematicStudyId)
        val systematicStudy = systematicStudyDto?.let { SystematicStudy.fromDto(it) }

        presenter.prepareIfFailsPreconditions(user, systematicStudy)
        if (presenter.isDone()) return

        if (request.reviewerId != request.userId) {
            presenter.prepareFailView(
                UnauthorizedUserException("A reviewer can only access the studies assigned to himself.")
            )
            return
        }

        val pageable = PageRequest.of(request.page, request.pageSize, parseSortParameter(request.sort))

        val filters = mapOf(
            "id" to request.id,
            "title" to request.title,
            "authors" to request.authors,
            "venue" to request.venue,
            "year" to request.year,
            "selectionStatus" to request.selectionStatus,
            "extractionStatus" to request.extractionStatus,
            "score" to request.score,
            "readingPriority" to request.readingPriority
        )

        val page = reviewerStudySearchRepository.findAllByAdvancedSearch(
            systematicStudyId = request.systematicStudyId,
            reviewerId = request.reviewerId,
            stage = request.stage,
            filters = filters,
            pageable = pageable
        )

        presenter.prepareSuccessView(
            FindAllStudyReviewsByAdvancedSearchService.ResponseModel(
                userId = request.userId,
                systematicStudyId = request.systematicStudyId,
                studyReviews = page.content.map { it.studyReview },
                page = pageable.pageNumber,
                size = pageable.pageSize,
                totalElements = page.totalElements,
                totalPages = page.totalPages
            )
        )
    }

    private fun parseSortParameter(sortParam: String): Sort {
        val parts = sortParam.split(",")
        val direction = if (parts.size > 1 && parts[1].equals("desc", ignoreCase = true)) {
            Sort.Direction.DESC
        } else Sort.Direction.ASC
        return Sort.by(direction, parts[0])
    }
}