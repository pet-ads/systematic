package br.all.application.search

import br.all.application.researcher.credentials.ResearcherCredentialsService
import br.all.application.search.repository.SearchSessionRepository
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.search.create.CreateSearchSessionPresenter
import br.all.application.search.create.CreateSearchSessionService
import br.all.application.search.create.CreateSearchSessionService.RequestModel
import br.all.application.shared.exceptions.UniquenessViolationException
import br.all.application.shared.presenter.PreconditionChecker
import br.all.application.study.repository.StudyReviewRepository
import br.all.application.study.repository.toDto
import br.all.domain.model.search.SearchSession
import br.all.domain.model.search.SearchSessionID
import br.all.domain.services.UuidGeneratorService
import br.all.domain.model.protocol.ProtocolId
import br.all.domain.model.researcher.ResearcherId
import br.all.domain.model.review.SystematicStudyId
import br.all.domain.services.BibtexConverterService
import java.util.*

class CreateSearchSessionServiceImpl(
    private val searchSessionRepository: SearchSessionRepository,
    private val systematicStudyRepository: SystematicStudyRepository,
    private val uuidGeneratorService: UuidGeneratorService,
    private val bibtexConverterService: BibtexConverterService,
    private val studyReviewRepository: StudyReviewRepository,
    private val credentialsService: ResearcherCredentialsService,
) : CreateSearchSessionService {
    override fun createSession(presenter: CreateSearchSessionPresenter, request: RequestModel) {

        val researcherId = ResearcherId(request.researcherId)
        val systematicStudy = SystematicStudyId(request.systematicStudyId)
        val preconditionChecker = PreconditionChecker(systematicStudyRepository, credentialsService)
        preconditionChecker.prepareIfViolatesPreconditions(presenter, researcherId, systematicStudy)

        if(presenter.isDone()) return

        require(request.searchString.isNotBlank()) { "Search string must not be blank" }

        val protocolId = ProtocolId(request.systematicStudyId)

        if (searchSessionRepository.existsBySearchSource(request.systematicStudyId, request.source.searchSource)) {
            presenter.prepareFailView((UniquenessViolationException("Search session already exists for source: ${request.source}")))
        }

        val sessionId = SearchSessionID(uuidGeneratorService.next())
        val searchSession = SearchSession(
            sessionId,
            systematicStudyId = systematicStudy,
            request.searchString,
            request.additionalInfo ?: "",
            source = request.source
        )

        val bibFileContent = String(request.bibFile.bytes)

        val studyReviews = bibtexConverterService.convertManyToStudyReview(systematicStudy, bibFileContent)

        studyReviewRepository.saveOrUpdateBatch(studyReviews.map { it.toDto() })

        searchSessionRepository.create(searchSession)
        presenter.prepareSuccessView(CreateSearchSessionService.ResponseModel(sessionId.value, systematicStudy, researcherId))
    }
}
