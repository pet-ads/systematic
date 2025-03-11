package br.all.application.search.update

import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.review.repository.fromDto
import br.all.application.search.repository.SearchSessionRepository
import br.all.application.search.update.PatchSearchSessionService.ResponseModel
import br.all.application.shared.exceptions.EntityNotFoundException
import br.all.application.shared.presenter.prepareIfFailsPreconditions
import br.all.application.study.repository.StudyReviewRepository
import br.all.application.study.repository.toDto
import br.all.application.user.CredentialsService
import br.all.domain.model.review.SystematicStudy
import br.all.domain.model.review.toSystematicStudyId
import br.all.domain.model.search.toSearchSessionID
import br.all.domain.services.ConverterFactoryService

class PatchSearchSessionServiceImpl (
    private val systematicStudyRepository: SystematicStudyRepository,
    private val searchSessionRepository: SearchSessionRepository,
    private val credentialsService: CredentialsService,
    private val studyReviewRepository: StudyReviewRepository,
    private val converterFactoryService: ConverterFactoryService
) : PatchSearchSessionService {
    override fun patchSession(
        presenter: PatchSearchSessionPresenter,
        request: PatchSearchSessionService.RequestModel,
        file: String
    ) {
        val user = credentialsService.loadCredentials(request.userId)?.toUser()

        val searchSessionDto = searchSessionRepository.findById(request.sessionId)
        val systematicStudyDto = systematicStudyRepository.findById(request.systematicStudyId)
        val systematicStudy = systematicStudyDto?.let { SystematicStudy.fromDto(it) }

        presenter.prepareIfFailsPreconditions(user, systematicStudy)

        if (presenter.isDone()) return

        if (searchSessionDto != null) {
            val (studyReviews, invalidEntries) = converterFactoryService.extractReferences(
                systematicStudyId = request.systematicStudyId.toSystematicStudyId(),
                searchSessionId = request.sessionId.toSearchSessionID(),
                file = file,
                source = mutableSetOf()
            )

            val studies = studyReviews.size
            searchSessionDto.numberOfRelatedStudies += studies
            searchSessionRepository.saveOrUpdate(searchSessionDto)
            studyReviewRepository.saveOrUpdateBatch(studyReviews.map { it.toDto() })

            presenter.prepareSuccessView(
                ResponseModel(
                    userId = request.userId,
                    systematicStudyId = request.systematicStudyId,
                    sessionId = request.sessionId,
                    invalidEntries = invalidEntries
                )
            )

        } else {
            val message = "There is no search session of id ${request.sessionId}"
            presenter.prepareFailView(EntityNotFoundException(message))
        }
    }
}