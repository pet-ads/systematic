package br.all.protocol.controller

import br.all.application.protocol.create.CreateProtocolService
import br.all.application.protocol.find.FindOneProtocolService
import br.all.application.protocol.repository.PicocDto
import br.all.application.protocol.update.UpdateProtocolService
import br.all.protocol.presenter.RestfulCreateProtocolPresenter
import br.all.protocol.presenter.RestfulFindOneProtocolPresenter
import br.all.protocol.presenter.RestfulUpdateProtocolPresenter
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*
import br.all.application.protocol.create.CreateProtocolService.RequestModel as CreateRequestModel
import br.all.application.protocol.find.FindOneProtocolService.RequestModel as FindOneRequestModel

@RestController
@RequestMapping("/researcher/{researcherId}/systematic-study/{systematicStudyId}/protocol")
class ProtocolController(
    private val createProtocolService: CreateProtocolService,
    private val findOneProtocolService: FindOneProtocolService,
    private val updateProtocolService: UpdateProtocolService,
) {
    @PostMapping
    @Operation(summary = "Create a protocol")
    @ApiResponses(value = [
        ApiResponse(responseCode = "201", description = "Successful Operation"),
        ApiResponse(responseCode = "404", description = "Failed Operation for nonexistent systematic study"),
        ApiResponse(responseCode = "403", description = "Failed Operation for non collaborator researcher writing protocols")
    ])
    fun postProtocol(
        @PathVariable researcherId: UUID,
        @PathVariable systematicStudyId: UUID,
        @RequestBody request: ProtocolRequest,
    ): ResponseEntity<*> {
        val presenter = RestfulCreateProtocolPresenter()
        val requestModel = request.toCreateRequestModel(researcherId, systematicStudyId)

        createProtocolService.create(presenter, requestModel)

        return presenter.responseEntity ?: ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @GetMapping
    @Operation(summary = "Find a protocol using its SystematicStudy Id")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Successful Operation"),
        ApiResponse(responseCode = "404", description = "Failed Operation for nonexistent protocol or systematic study"),
        ApiResponse(responseCode = "403", description = "Failed Operation for non collaborator researcher finding protocols")
    ])
    fun findById(@PathVariable researcherId: UUID, @PathVariable systematicStudyId: UUID): ResponseEntity<*> {
        val presenter = RestfulFindOneProtocolPresenter()
        val request = FindOneRequestModel(researcherId, systematicStudyId)

        findOneProtocolService.findById(presenter, request)
        return presenter.responseEntity ?: ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @PutMapping
    @Operation(summary = "Update an existing protocol")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Successful Operation"),
        ApiResponse(responseCode = "404", description = "Failed Operation for nonexistent systematic study"),
        ApiResponse(responseCode = "403", description = "Failed Operation for non collaborator researcher updating protocols")
    ])
    fun putProtocol(
        @PathVariable researcherId: UUID,
        @PathVariable systematicStudyId: UUID,
        @RequestBody request: ProtocolRequest,
    ): ResponseEntity<*> {
        val presenter = RestfulUpdateProtocolPresenter()
        val requestModel = request.toUpdateRequestModel(researcherId, systematicStudyId)

        updateProtocolService.update(presenter, requestModel)
        return presenter.responseEntity ?: ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR)
    }

    data class ProtocolRequest(
        val goal: String? = null,
        val justification: String? = null,
        val researchQuestions: Set<String>,
        val keywords: Set<String> = emptySet(),

        val searchString: String? = null,
        val informationSources: Set<String> = emptySet(),
        val sourcesSelectionCriteria: String? = null,
        val searchMethod: String? = null,

        val studiesLanguages: Set<String> = emptySet(),
        val studyTypeDefinition: String? = null,

        val selectionProcess: String? = null,
        val eligibilityCriteria: Set<Pair<String, String>> = emptySet(),

        val dataCollectionProcess: String? = null,
        val analysisAndSynthesisProcess: String? = null,

        val picoc: PicocRequest? = null,
    ) {
        fun toCreateRequestModel(researcher: UUID, systematicStudy: UUID) = CreateRequestModel(
            researcher,
            systematicStudy,
            goal,
            justification,
            researchQuestions,
            keywords,
            searchString,
            informationSources,
            sourcesSelectionCriteria,
            searchMethod,
            studiesLanguages,
            studyTypeDefinition,
            selectionProcess,
            eligibilityCriteria,
            dataCollectionProcess,
            analysisAndSynthesisProcess,
            picoc?.let { PicocDto(it.population, it.intervention, it.control, it.outcome, it.context) },
        )

        fun toUpdateRequestModel(researcher: UUID, systematicStudy: UUID) = UpdateProtocolService.RequestModel(
            researcher,
            systematicStudy,
            goal,
            justification,
            researchQuestions,
            keywords,
            searchString,
            informationSources,
            sourcesSelectionCriteria,
            searchMethod,
            studiesLanguages,
            studyTypeDefinition,
            selectionProcess,
            eligibilityCriteria,
            dataCollectionProcess,
            analysisAndSynthesisProcess,
            picoc?.let { PicocDto(it.population, it.intervention, it.control, it.outcome, it.context) },
        )

        data class PicocRequest(
            val population: String,
            val intervention: String,
            val control: String,
            val outcome: String,
            val context: String? = null,
        )
    }
}
