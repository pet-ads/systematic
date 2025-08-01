package br.all.application.user.find

import java.util.UUID

interface RetrieveAccountPublicDataService {
    fun retrieveData(presenter: RetrieveAccountPublicDataPresenter, request: RequestModel)

    data class RequestModel(
        val userId: UUID
    )

    data class ResponseModel(
        val userId: UUID,
        val username: String,
        val email: String,
        val affiliation: String,
        val country: String
    )
}