package br.all.application.search.create

import br.all.application.collaboration.repository.CollaborationRepository
import br.all.application.collaboration.repository.toDomain
import br.all.application.protocol.repository.ProtocolRepository
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.review.repository.fromDto
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
import br.all.domain.services.ConverterFactoryService
import br.all.domain.services.ReviewSimilarityService
import br.all.domain.services.ScoreCalculatorService
import br.all.domain.services.UuidGeneratorService

class CreateSearchSessionServiceImpl(
    private val searchSessionRepository: SearchSessionRepository,
    private val systematicStudyRepository: SystematicStudyRepository,
    private val protocolRepository: ProtocolRepository,
    private val uuidGeneratorService: UuidGeneratorService,
    private val converterFactoryService: ConverterFactoryService,
    private val studyReviewRepository: StudyReviewRepository,
    private val credentialsService: CredentialsService,
    private val scoreCalculatorService: ScoreCalculatorService,
    private val reviewSimilarityService: ReviewSimilarityService,
    private val collaborationRepository: CollaborationRepository
) : CreateSearchSessionService {

    override fun createSession(
        presenter: CreateSearchSessionPresenter,
        request: RequestModel,
        file: String
    ) {
        val user = credentialsService.loadCredentials(request.userId)?.toUser()

        val systematicStudyDto = systematicStudyRepository.findById(request.systematicStudyId)
        val systematicStudy = systematicStudyDto?.let { SystematicStudy.fromDto(it) }
        val collaborations = collaborationRepository
            .listAllCollaborationsBySystematicStudyId(request.systematicStudyId)
            .map { it.toDomain() }

        presenter.prepareIfFailsPreconditions(user, systematicStudy, collaborations = collaborations)

        if (presenter.isDone()) return

        val source = request.source

        val protocolDto = protocolRepository.findById(request.systematicStudyId)

        if (protocolDto == null) {
            presenter.prepareFailView(NoSuchElementException("Protocol ${request.systematicStudyId} not found"))
            return
        }

        val hasSource = protocolDto.informationSources.contains(source)

        if (!hasSource) {
            val message = "Protocol ID ${protocolDto.id} does not contain $source as a search source"
            presenter.prepareFailView(NoSuchElementException(message))
            return
        }

        val sessionId = SearchSessionID(uuidGeneratorService.next())
        val searchSession = SearchSession.fromRequestModel(sessionId, request)

        val (studyReviews, invalidEntries) = converterFactoryService.extractReferences(
            request.systematicStudyId.toSystematicStudyId(),
            sessionId,
            file,
            mutableSetOf(source)
        )

        /*
        criar os request StudyInfo a partir dos studyReviews (infos)
        val selectionSuggestions = selectionSuggestion.buildSuggestions(infos)
        studyReviews.foreach(it.setSuggestedStatus(selectionsuggstion.filter(it.getId)
         */

        val scoredStudyReviews = scoreCalculatorService.applyScoreToManyStudyReviews(studyReviews, protocolDto.keywords)

        studyReviewRepository.saveOrUpdateBatch(scoredStudyReviews.map { it.toDto() })

        val duplicatedAnalysedReviews = reviewSimilarityService.findDuplicates(scoredStudyReviews, emptyList())
        val toSaveDuplicatedAnalysedReviews = duplicatedAnalysedReviews
            .flatMap { (key, value) -> listOf(key) + value }
            .toList()

        studyReviewRepository.saveOrUpdateBatch(toSaveDuplicatedAnalysedReviews.map { it.toDto() })

        val numberOfRelatedStudies = studyReviews.size
        searchSession.numberOfRelatedStudies = numberOfRelatedStudies

        searchSessionRepository.create(searchSession.toDto())

        presenter.prepareSuccessView(
            ResponseModel(
                userId = request.userId,
                systematicStudyId = request.systematicStudyId,
                sessionId = sessionId.value,
                invalidEntries = invalidEntries
            )
        )
    }
}
