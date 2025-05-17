package br.all.application.report.find.service

import br.all.application.report.find.presenter.FindKeywordsPresenter
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.review.repository.fromDto
import br.all.application.shared.presenter.prepareIfFailsPreconditions
import br.all.application.study.repository.StudyReviewRepository
import br.all.application.user.CredentialsService
import br.all.domain.model.review.SystematicStudy
import br.all.domain.model.study.ExtractionStatus
import br.all.domain.model.study.SelectionStatus

class FindKeywordsServiceImpl(
    private val systematicStudyRepository: SystematicStudyRepository,
    private val studyReviewRepository: StudyReviewRepository,
    private val credentialsService: CredentialsService,
): FindKeywordsService {
    override fun findKeywords(presenter: FindKeywordsPresenter, request: FindKeywordsService.RequestModel) {
        val user = credentialsService.loadCredentials(request.userId)?.toUser()

        val systematicStudyDto = systematicStudyRepository.findById(request.systematicStudyId)
        val systematicStudy = systematicStudyDto?.let { SystematicStudy.fromDto(it) }

        presenter.prepareIfFailsPreconditions(user, systematicStudy)

        if(presenter.isDone()) return

        val allStudies = studyReviewRepository.findAllFromReview(request.systematicStudyId)

        val response = FindKeywordsService.ResponseModel(
            request.userId,
            request.systematicStudyId,
            request.filter,
            keywords = emptyMap(),
            keywordsQuantity = 0,
        )

        when (request.filter) {
            "selection" -> {
                val selectionKeywords = allStudies.asSequence()
                    .filter  { it.selectionStatus == SelectionStatus.INCLUDED.name }
                    .map     { it.keywords }
                    .flatten()
                    .flatMap { it.split(";") }
                    .map     { it.trim() }
                    .filter  { it.isNotEmpty() }
                    .groupingBy { it }
                    .eachCount()
                    .toSortedMap()
                    .toMap()

                response.keywords = selectionKeywords
                response.keywordsQuantity = selectionKeywords.values.sum()
            }

            "extraction" -> {
                val extractionKeywords = allStudies
                    .asSequence()
                    .filter  { it.extractionStatus == ExtractionStatus.INCLUDED.toString() }
                    .map     { it.keywords }
                    .flatten()
                    .flatMap { it.split(";") }
                    .map     { it.trim() }
                    .filter  { it.isNotEmpty() }
                    .groupingBy { it }
                    .eachCount()
                    .toSortedMap()
                    .toMap()

                response.keywords = extractionKeywords
                response.keywordsQuantity = extractionKeywords.size
            }
            null -> {
                val allKeywords = allStudies
                    .asSequence()
                    .map     { it.keywords }
                    .flatten()
                    .flatMap { it.split(";") }
                    .map     { it.trim() }
                    .filter  { it.isNotEmpty() }
                    .groupingBy { it }
                    .eachCount()
                    .toSortedMap()
                    .toMap()

                response.keywords = allKeywords
                response.keywordsQuantity = allKeywords.size
            }
            else -> {
                presenter.prepareFailView(IllegalArgumentException(request.filter))
                if (presenter.isDone()) return
            }
        }

        presenter.prepareSuccessView(response)
    }
}