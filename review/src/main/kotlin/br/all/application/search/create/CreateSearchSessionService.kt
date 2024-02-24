package br.all.application.search.create

import br.all.domain.model.protocol.SearchSource
import br.all.domain.model.researcher.ResearcherId
import br.all.domain.model.review.SystematicStudyId
import org.springframework.web.multipart.MultipartFile
import java.util.*

interface CreateSearchSessionService {
    fun createSession(presenter: CreateSearchSessionPresenter, request: RequestModel, file: String)


    data class RequestModel(
        val researcherId: UUID,
        val systematicStudyId: UUID,
        val source: String,
        val searchString: String,
        val additionalInfo: String?,
    )

    data class ResponseModel(
        val sessionId: UUID,
        val systematicStudyId: SystematicStudyId,
        val researcherId: ResearcherId
    )
}