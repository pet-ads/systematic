package br.all.review.requests

import br.all.application.review.update.services.UpdateSystematicStudyService
import java.util.*

data class PutRequest(val title: String?, val description: String?) {
    fun toUpdateRequestModel(researcherId: UUID, systematicStudyId: UUID) =
        UpdateSystematicStudyService.RequestModel(researcherId, systematicStudyId, title, description)
}