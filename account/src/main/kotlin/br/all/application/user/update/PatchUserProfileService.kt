package br.all.application.user.update

import java.util.UUID

interface PatchUserProfileService {
    fun patchProfile(presenter: PatchUserProfilePresenter, request: RequestModel)

    data class RequestModel(
        val userId: UUID,
        val name: String,
        val email: String,
        val affiliation: String,
        val country: String
    )

    data class ResponseModel(
        val userId: UUID,
        val name: String,
        val username: String,
        val email: String,
        val affiliation: String,
        val country: String,
        val invalidEntries: List<InvalidEntry>
    )

    data class InvalidEntry(
        val field: String,
        val entry: String,
        val message: String
    )
}