package br.all.application.search.create

import br.all.domain.model.protocol.SearchSource
import br.all.domain.model.researcher.ResearcherId
import br.all.domain.model.review.SystematicStudyId
import org.springframework.web.multipart.MultipartFile
import java.util.*

interface CreateSearchSessionService {
    fun createSession(presenter: CreateSearchSessionPresenter, request: RequestModel)

    data class RequestModel(
        val researcherId: UUID,
        val systematicStudyId: UUID,
        val source: SearchSource,
        val searchString: String,
        val additionalInfo: String?,
        val bibFile: MultipartFile
    )

    data class ResponseModel(
        val sessionId: UUID,
        val systematicStudyId: SystematicStudyId,
        val researcherId: ResearcherId
    )
}