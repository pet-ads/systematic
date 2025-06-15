package br.all.review.requests

import br.all.application.review.create.CreateSystematicStudyService.RequestModel
import java.util.*

data class PostRequest(
    val title: String,
    val description: String,
    val collaborators: Set<UUID>,
) {
    fun toCreateRequestModel(userId: UUID) =
        RequestModel(userId, title, description)
}