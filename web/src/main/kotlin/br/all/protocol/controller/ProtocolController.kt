package br.all.protocol.controller

import br.all.application.protocol.find.FindOneProtocolService
import br.all.application.protocol.repository.CriterionDto
import br.all.application.protocol.repository.PicocDto
import br.all.application.protocol.update.UpdateProtocolService
import br.all.protocol.presenter.RestfulFindOneProtocolPresenter
import br.all.protocol.presenter.RestfulUpdateProtocolPresenter
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*
import br.all.application.protocol.find.FindOneProtocolService.RequestModel as FindOneRequestModel

@RestController
@RequestMapping("/researcher/{researcherId}/systematic-study/{systematicStudyId}/protocol")
class ProtocolController(
    private val findOneProtocolService: FindOneProtocolService,
    private val updateProtocolService: UpdateProtocolService,
) {
    @GetMapping
    fun findById(@PathVariable researcherId: UUID, @PathVariable systematicStudyId: UUID): ResponseEntity<*> {
        val presenter = RestfulFindOneProtocolPresenter()
        val request = FindOneRequestModel(researcherId, systematicStudyId)

        findOneProtocolService.findById(presenter, request)
        return presenter.responseEntity ?: ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @PutMapping
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
        val eligibilityCriteria: Set<CriterionDto> = emptySet(),

        val dataCollectionProcess: String? = null,
        val analysisAndSynthesisProcess: String? = null,

        val picoc: PicocRequest? = null,
    ) {
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
