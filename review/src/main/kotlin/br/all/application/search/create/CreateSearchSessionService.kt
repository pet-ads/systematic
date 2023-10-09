package br.all.application.search.create

import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.search.find.SearchSessionResponseModel
import br.all.application.search.repository.SearchSessionRepository
import br.all.domain.model.search.SearchSession
import br.all.domain.model.search.SearchSessionID
import br.all.domain.services.UuidGeneratorService

class CreateSearchSessionService(
    private val systematicStudyRepository: SystematicStudyRepository,
    private val searchSessionRepository: SearchSessionRepository,
    private val uuidGeneratorService: UuidGeneratorService
) {
    fun createSession(requestModel: SearchSessionRequestModel): SearchSessionResponseModel {
        require(requestModel.searchString.isNotBlank()) { "Search string must not be blank" }
        val existingSession = searchSessionRepository.getSessionBySource(requestModel.source)
        //implements a better logic
        require(existingSession == null) { "A session for this SearchSource already exists" }

        val sessionId = SearchSessionID(uuidGeneratorService.next())

        val searchSession = SearchSession(
            sessionId,
            requestModel.searchString,
            requestModel.additionalInfo ?: "",
            source = requestModel.source
        )

        searchSessionRepository.create(searchSession)

        return SearchSessionResponseModel(sessionId.toString(), "Search session created successfully.")
    }
}
