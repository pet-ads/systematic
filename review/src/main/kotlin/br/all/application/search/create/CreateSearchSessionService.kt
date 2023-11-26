package br.all.application.search

import br.all.application.search.create.SearchSessionRequestModel
import br.all.application.search.find.SearchSessionResponseModel
import br.all.application.search.repository.SearchSessionRepository
import br.all.application.review.repository.SystematicStudyRepository
import br.all.domain.model.search.SearchSession
import br.all.domain.model.search.SearchSessionID
import br.all.domain.services.UuidGeneratorService
import br.all.domain.model.protocol.ProtocolId
import java.util.*
import kotlin.NoSuchElementException

class CreateSearchSessionService(
    private val searchSessionRepository: SearchSessionRepository,
    private val systematicStudyRepository: SystematicStudyRepository,
    private val uuidGeneratorService: UuidGeneratorService
) {
    fun createSession(requestModel: SearchSessionRequestModel): SearchSessionResponseModel {
        require(requestModel.searchString.isNotBlank()) { "Search string must not be blank" }

        val systematicStudy = systematicStudyRepository.findById(requestModel.reviewId)
            ?: throw NoSuchElementException("Systematic study not found with ID: ${requestModel.reviewId}")

        val protocolId = ProtocolId(systematicStudy.id)

        if (searchSessionRepository.getSearchSessionBySource(protocolId, requestModel.source) != null) {
            throw IllegalStateException("Search session already exists for source: ${requestModel.source}")
        }

        val sessionId = SearchSessionID(uuidGeneratorService.next())
        val searchSession = SearchSession(
            sessionId,
            protocolId = ProtocolId(UUID.randomUUID()),
            requestModel.searchString,
            requestModel.additionalInfo ?: "",
            source = requestModel.source
        )

        searchSessionRepository.create(searchSession)
        return SearchSessionResponseModel(sessionId.toString(), "Search session created successfully.")
    }
}
