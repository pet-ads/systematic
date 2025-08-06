package br.all.application.user.find
import java.time.LocalDateTime
import java.util.UUID

interface RetrieveUserProfileService {
    fun retrieveData(presenter: RetrieveUserProfilePresenter, request: RequestModel)

    data class RequestModel(
        val userId: UUID
    )

    data class ResponseModel(
        val userId: UUID,
        val username: String,
        val email: String,
        val affiliation: String,
        val country: String,
        val authorities: Set<String>,
    )
}