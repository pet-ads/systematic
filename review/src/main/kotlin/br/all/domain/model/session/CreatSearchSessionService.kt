package br.all.application.session

import br.all.domain.model.session.SearchSessionRequestModel
import br.all.domain.model.session.SearchSessionResponseModel
import br.all.domain.model.session.SearchSessionAGRE
import br.all.domain.model.session.SearchSessionID
//import br.all.domain.model.session.SearchSessionRepository
import br.all.domain.services.UuidGeneratorService
import br.all.domain.shared.ddd.Notification
import java.time.LocalDateTime

class SearchSessionApplicationService(
    //private val searchSessionRepository: SearchSessionRepository,
    private val uuidGeneratorService: UuidGeneratorService
) {
    fun createSession(requestModel: SearchSessionRequestModel): SearchSessionResponseModel {

        val validationNotification = validateInput(requestModel)
        if (validationNotification.addError("An error has been detected at create your search session\n")) {
            return SearchSessionResponseModel("", validationNotification.message())
        }
        val sessionId = SearchSessionID(uuidGeneratorService.next())
        val searchSession = SearchSessionAGRE(
            sessionId,
            requestModel.searchString,
            requestModel.additionalInfo,
            LocalDateTime.now(),
            requestModel.researchers
        )
        //searchSessionRepository.create(searchSession)

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


