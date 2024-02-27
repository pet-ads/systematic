package br.all.protocol.controller

import br.all.application.protocol.create.CreateProtocolServiceImpl
import br.all.application.protocol.find.FindOneProtocolServiceImpl
import br.all.protocol.presenter.RestfulCreateProtocolPresenter
import br.all.protocol.presenter.RestfulFindOneProtocolPresenter
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*
import br.all.application.protocol.create.CreateProtocolService.RequestModel as CreateRequestModel
import br.all.application.protocol.find.FindOneProtocolService.RequestModel as FindOneRequestModel

@RestController
@RequestMapping("/researcher/{researcherId}/systematic-study/{systematicStudyId}/protocol")
class ProtocolController(
    private val createProtocolService: CreateProtocolServiceImpl,
    private val findOneProtocolService: FindOneProtocolServiceImpl,
) {
    @PostMapping
    fun postProtocol(
        @PathVariable researcherId: UUID,
        @PathVariable systematicStudyId: UUID,
        @RequestBody request: PostRequest,
    ): ResponseEntity<*> {
        val presenter = RestfulCreateProtocolPresenter()
        val requestModel = request.toCreateRequestModel(researcherId, systematicStudyId)

        createProtocolService.create(presenter, requestModel)

        return presenter.responseEntity ?: ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR)
    }

    data class PostRequest(
        val goal: String? = null,
        val justification: String? = null,
        val keywords: Set<String> = emptySet(),

        val searchString: String? = null,
        val informationSources: Set<String> = emptySet(),
        val sourcesSelectionCriteria: String? = null,
        val searchMethod: String? = null,

        val studiesLanguages: Set<String> = emptySet(),
        val studyTypeDefinition: String? = null,

        val selectionProcess: String? = null,
        val dataCollectionProcess: String? = null,
        val analysisAndSynthesisProcess: String? = null,
    ) {
        fun toCreateRequestModel(researcher: UUID, systematicStudy: UUID) = CreateRequestModel(
            researcher,
            systematicStudy,
            goal,
            justification,
            keywords,
            searchString,
            informationSources,
            sourcesSelectionCriteria,
            searchMethod,
            studiesLanguages,
            studyTypeDefinition,
            selectionProcess,
            dataCollectionProcess,
            analysisAndSynthesisProcess,
        )
    }

    @GetMapping
    fun findById(@PathVariable researcherId: UUID, @PathVariable systematicStudyId: UUID): ResponseEntity<*> {
        val presenter = RestfulFindOneProtocolPresenter()
        val request = FindOneRequestModel(researcherId, systematicStudyId)

        findOneProtocolService.findById(presenter, request)
        return presenter.responseEntity ?: ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR)
    }
}
