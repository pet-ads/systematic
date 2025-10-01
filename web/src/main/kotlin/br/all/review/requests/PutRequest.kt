package br.all.review.requests

import br.all.application.review.update.services.UpdateSystematicStudyService
import io.swagger.v3.oas.annotations.media.Schema
import java.util.*

@Schema(name = "SystematicStudyPutRequest")
data class PutRequest(val title: String?, val description: String?, val objectives: String?) {
    fun toUpdateRequestModel(researcherId: UUID, systematicStudyId: UUID) =
        UpdateSystematicStudyService.RequestModel(researcherId, systematicStudyId, title, description, objectives)
}