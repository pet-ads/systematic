package br.all.application.study.find.service

import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.review.repository.fromDto
import br.all.application.shared.presenter.prepareIfFailsPreconditions
import br.all.application.study.find.presenter.FindAllStudyReviewsByAdvancedSearchPresenter
import br.all.application.study.repository.StudyReviewRepository
import br.all.application.user.CredentialsService
import br.all.domain.model.review.SystematicStudy
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort

class FindAllStudyReviewsByAdvancedSearchServiceImpl(
    private val systematicStudyRepository: SystematicStudyRepository,
    private val studyReviewRepository: StudyReviewRepository,
    private val credentialsService: CredentialsService
) : FindAllStudyReviewsByAdvancedSearchService {

    private fun parseSortParameter(sortParam: String): Sort {
        val parts = sortParam.split(",")
        val property = parts[0]
        val direction = if (parts.size > 1 && parts[1].equals("desc", ignoreCase = true)) {
            Sort.Direction.DESC
        } else Sort.Direction.ASC
        return Sort.by(direction, property)
    }

    override fun findAllByAdvancedSearch(
        presenter: FindAllStudyReviewsByAdvancedSearchPresenter,
        request: FindAllStudyReviewsByAdvancedSearchService.RequestModel
    ) {
        val user = credentialsService.loadCredentials(request.userId)?.toUser()
        val systematicStudyDto = systematicStudyRepository.findById(request.systematicStudyId)
        val systematicStudy = systematicStudyDto?.let { SystematicStudy.fromDto(it) }

        presenter.prepareIfFailsPreconditions(user, systematicStudy)
        if (presenter.isDone()) return

        val sort = parseSortParameter(request.sort)
        val pageable = PageRequest.of(request.page, request.pageSize, sort)

        val filters = mapOf(
            "id" to request.id,
            "studyReviewId" to request.studyReviewId,
            "title" to request.title,
            "authors" to request.authors,
            "venue" to request.venue,
            "year" to request.year,
            "selectionStatus" to request.selectionStatus,
            "extractionStatus" to request.extractionStatus,
            "score" to request.score,
            "readingPriority" to request.readingPriority
        )

        val pageResult = studyReviewRepository.findAllByAdvancedSearch(
            reviewId = request.systematicStudyId,
            filters = filters,
            pageable = pageable
        )

        presenter.prepareSuccessView(
            FindAllStudyReviewsByAdvancedSearchService.ResponseModel(
                userId = request.userId,
                systematicStudyId = request.systematicStudyId,
                studyReviews = pageResult.content,
                page = pageable.pageNumber,
                size = pageable.pageSize,
                totalElements = pageResult.totalElements,
                totalPages = pageResult.totalPages
            )
        )
    }
}
