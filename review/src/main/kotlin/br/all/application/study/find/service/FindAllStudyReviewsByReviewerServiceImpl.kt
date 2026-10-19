package br.all.application.study.find.service

import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.review.repository.fromDto
import br.all.application.reviewmode.repository.ReviewerStudyRepository
import br.all.application.shared.presenter.prepareIfFailsPreconditions
import br.all.application.study.find.presenter.FindAllStudyReviewsByReviewerPresenter
import br.all.application.study.find.service.FindAllStudyReviewsByReviewerService.RequestModel
import br.all.application.study.find.service.FindAllStudyReviewsByReviewerService.ResponseModel
import br.all.application.user.CredentialsService
import br.all.domain.model.review.SystematicStudy
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort

class FindAllStudyReviewsByReviewerServiceImpl(
    private val systematicStudyRepository: SystematicStudyRepository,
    private val reviewerStudyRepository: ReviewerStudyRepository,
    private val credentialsService: CredentialsService,
) : FindAllStudyReviewsByReviewerService {

    override fun findAllByReviewer(presenter: FindAllStudyReviewsByReviewerPresenter, request: RequestModel) {
        val user = credentialsService.loadCredentials(request.userId)?.toUser()

        val systematicStudyDto = systematicStudyRepository.findById(request.systematicStudyId)
        val systematicStudy = systematicStudyDto?.let { SystematicStudy.fromDto(it) }

        presenter.prepareIfFailsPreconditions(user, systematicStudy)
        if (presenter.isDone()) return

        val pageable = PageRequest.of(request.page, request.pageSize, parseSortParameter(request.sort))
        val reviewerStudies = reviewerStudyRepository.findAllByReviewerPaged(
            request.systematicStudyId, request.reviewerId, request.stage, pageable
        )

        presenter.prepareSuccessView(
            ResponseModel(
                userId = request.userId,
                systematicStudyId = request.systematicStudyId,
                reviewerId = request.reviewerId,
                stage = request.stage,
                studyReviews = reviewerStudies.content.map { it.studyReview },
                page = pageable.pageNumber,
                size = pageable.pageSize,
                totalElements = reviewerStudies.totalElements,
                totalPages = reviewerStudies.totalPages
            )
        )
    }

    private fun parseSortParameter(sortParam: String): Sort {
        val parts = sortParam.split(",")
        val direction = if (parts.size > 1 && parts[1].equals("desc", ignoreCase = true)) {
            Sort.Direction.DESC
        } else {
            Sort.Direction.ASC
        }
        return Sort.by(direction, parts[0])
    }
}