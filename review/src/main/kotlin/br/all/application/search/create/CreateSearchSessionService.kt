import br.all.application.protocol.repository.ProtocolRepository
import br.all.application.search.create.SearchSessionRequestModel
import br.all.application.search.create.CreateSessionResponseModel
import br.all.application.search.repository.SearchSessionMapper.toDto
import br.all.application.search.repository.SearchSessionRepository
import br.all.domain.model.protocol.ProtocolId
import br.all.domain.model.search.SearchSession
import br.all.domain.model.search.SearchSessionID
import br.all.domain.services.UuidGeneratorService

class CreateSearchSessionService(
        private val protocolRepository: ProtocolRepository,
        private val searchSessionRepository: SearchSessionRepository,
        private val uuidGeneratorService: UuidGeneratorService) {

    fun createSession(request: SearchSessionRequestModel): CreateSessionResponseModel {
        if(request.searchString.isNotBlank())
            throw IllegalArgumentException("Search string must not be black")

        val protocolId = ProtocolId(request.reviewId)

        val protocol = protocolRepository.findById(protocolId)
                ?: throw NoSuchElementException("Could not find a protocol of ID: ${request.systematicStudyId}")

        if (protocol.hasSource(request.source))
            throw NoSuchElementException("There is not such search source in current protocol: ${request.source}")

        searchSessionRepository.getSearchSessionBySource(protocolId, request.source)
                ?: throw IllegalStateException("Search session already exists")

        val sessionId = SearchSessionID(uuidGeneratorService.next())

        //TODO: WE WILL INSERT CODE HERE TO INTEGRATE YOU CODE TO SAULOS CODE.
        val searchSession = SearchSession(sessionId,
                protocolId,
                request.searchString,
                request.additionalInfo ?: "",
                source = request.source,
        )
        //TODO do not use domain entities as parameters of return values of repository interfaces. Only use dtos
        searchSessionRepository.create(searchSession.toDto())
        return CreateSessionResponseModel(sessionId.toString())
    }
}
