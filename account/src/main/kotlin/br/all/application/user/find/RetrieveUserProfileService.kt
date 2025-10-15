package br.all.application.user.find
import io.swagger.v3.oas.annotations.media.Schema
import java.util.UUID

interface RetrieveUserProfileService {
    fun retrieveData(presenter: RetrieveUserProfilePresenter, request: RequestModel)

    data class RequestModel(
        val userId: UUID
    )

    @Schema(name = "RetrieveUserProfileService", description = "Response model for Retrieve User Profile Service")
    data class ResponseModel(
        val userId: UUID,
        val name: String,
        val username: String,
        val email: String,
        val affiliation: String,
        val country: String,
        val authorities: Set<String>,
    )
}