package br.all.application.search

import br.all.domain.model.search.SearchSession
import br.all.domain.model.search.SearchSessionID
import br.all.domain.services.UuidGeneratorService
import br.all.domain.shared.ddd.Notification

class CreateSearchSessionService (
        //private val searchSessionRepository: SearchSessionRepository,
        private val uuidGeneratorService: UuidGeneratorService
    ) {
        fun createSession(requestModel: SearchSessionRequestModel): SearchSessionResponseModel {

            require(requestModel.searchString.isNotBlank()) {"Search string must not be blank"}

            //TODO use a SystematicStudyRepository check if the systematic study is valid
            //TODO create and use a SearchSessionRepository to check wheter a session for the SearchSource has
            // already been created. If so, throw an IllegalStateException.

            val sessionId = SearchSessionID(uuidGeneratorService.next())

            //TODO this service will also create all studies based on a bibtex file. Saulo is creating a parser for that
            val searchSession = SearchSession(
                sessionId,
                requestModel.searchString,
                requestModel.additionalInfo ?: "",
                source = requestModel.source
            )

            //TODO yeap, save the searchSession
            //searchSessionRepository.create(searchSession)


            return SearchSessionResponseModel(sessionId.toString(), "Search session created successfully.")
        }

        //TODO as we have only one thing to verify, you can use the build in require feature provided by Kotlin.
        //this one can be removed.
        private fun validateInput(requestModel: SearchSessionRequestModel): Notification {
            val notification = Notification()

            if (requestModel.searchString.isBlank()) {
                notification.addError("Search string cannot be empty.")
            }

            return notification
        }
    }

