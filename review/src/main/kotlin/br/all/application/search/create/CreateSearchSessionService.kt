import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.search.create.SearchSessionRequestModel
import br.all.application.search.find.SearchSessionResponseModel
import br.all.application.search.repository.SearchSessionRepository
import br.all.domain.model.search.SearchSession
import br.all.domain.model.search.SearchSessionID
import br.all.domain.services.UuidGeneratorService

class CreateSearchSessionService (
    private val searchSessionRepository: SearchSessionRepository,
    private val systematicStudyRepository: SystematicStudyRepository,
    private val uuidGeneratorService: UuidGeneratorService
) {
    fun createSession(requestModel: SearchSessionRequestModel): SearchSessionResponseModel {
        require(requestModel.searchString.isNotBlank()) { "Search string must not be blank" }

        val systematicStudy = systematicStudyRepository.findById(requestModel.reviewId)
        requireNotNull(systematicStudy) { "Systematic study not found with ID: ${requestModel.reviewId}" }

        val protocolId = systematicStudy.id// Obtenha o protocolId do SystematicStudy

        // Verifique se uma sessão para a SearchSource já foi criada usando o SearchSessionRepository
        val existingSearchSession = searchSessionRepository.getSearchSessionBySource(protocolId, requestModel.source)
        if (existingSearchSession != null) {
            throw IllegalStateException("Search session already exists for source: ${requestModel.source}")
        }

        val sessionId = SearchSessionID(uuidGeneratorService.next())

        val searchSession = SearchSession(
            sessionId,
            protocolId,
            requestModel.searchString,
            requestModel.additionalInfo ?: "",
            source = requestModel.source
        )

        // TODO: Salve a sessão de pesquisa usando o searchSessionRepository
        // searchSessionRepository.create(searchSession)

        return SearchSessionResponseModel(sessionId.toString(), "Search session created successfully.")
    }

    private fun validateInput(requestModel: SearchSessionRequestModel): Notification {
        val notification = Notification()

        if (requestModel.searchString.isBlank()) {
            notification.addError("Search string cannot be empty.")
        }

        return notification
    }
}
