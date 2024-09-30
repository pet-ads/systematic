package br.all.application.search

import br.all.application.protocol.repository.ProtocolRepository
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.review.repository.fromDto
import br.all.application.search.create.CreateSearchSessionPresenter
import br.all.application.search.create.CreateSearchSessionService
import br.all.application.search.create.CreateSearchSessionService.RequestModel
import br.all.application.search.create.CreateSearchSessionService.ResponseModel
import br.all.application.search.repository.SearchSessionRepository
import br.all.application.search.repository.fromRequestModel
import br.all.application.search.repository.toDto
import br.all.application.shared.presenter.prepareIfFailsPreconditions
import br.all.application.study.repository.StudyReviewRepository
import br.all.application.study.repository.toDto
import br.all.application.user.CredentialsService
import br.all.domain.model.review.SystematicStudy
import br.all.domain.model.review.toSystematicStudyId
import br.all.domain.model.search.SearchSession
import br.all.domain.model.search.SearchSessionID
import br.all.domain.services.BibtexConverterService
import br.all.domain.services.UuidGeneratorService

class CreateSearchSessionServiceImpl(
    private val searchSessionRepository: SearchSessionRepository,
    private val systematicStudyRepository: SystematicStudyRepository,
    private val protocolRepository: ProtocolRepository,
    private val uuidGeneratorService: UuidGeneratorService,
    private val bibtexConverterService: BibtexConverterService,
    private val studyReviewRepository: StudyReviewRepository,
    private val credentialsService: CredentialsService,
) : CreateSearchSessionService {


    override fun createSession(presenter: CreateSearchSessionPresenter, request: RequestModel, file: String) {

        val user = credentialsService.loadCredentials(request.userId)?.toUser()

        val systematicStudyDto = systematicStudyRepository.findById(request.systematicStudyId)
        val systematicStudy = systematicStudyDto?.let { SystematicStudy.fromDto(it) }
        presenter.prepareIfFailsPreconditions(user, systematicStudy)

        if (presenter.isDone()) return

        val source = request.source

        val protocolDto = protocolRepository.findById(request.systematicStudyId)
        val hasSource = protocolDto?.informationSources?.contains(source) ?: false

        if (!hasSource) {
            val message = "Protocol ID ${protocolDto?.id} does not contain $source as a search source"
            presenter.prepareFailView(NoSuchElementException(message))
        }

        val sessionId = SearchSessionID(uuidGeneratorService.next())
        val searchSession = SearchSession.fromRequestModel(sessionId, request)

        val studyReviews = bibtexConverterService.convertManyToStudyReview(request.systematicStudyId.toSystematicStudyId(), sessionId, file)
        studyReviewRepository.saveOrUpdateBatch(studyReviews.map { it.toDto() })

        searchSessionRepository.create(searchSession.toDto())
        presenter.prepareSuccessView(ResponseModel(request.userId, request.systematicStudyId, sessionId.value))
    }
}
