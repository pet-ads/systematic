package br.all.review.controller

import br.all.application.review.create.CreateSystematicStudyService
import br.all.application.review.find.services.FindAllSystematicStudiesService
import br.all.application.review.find.services.FindOneSystematicStudyService
import br.all.application.review.update.services.UpdateSystematicStudyService
import br.all.review.presenter.RestfulCreateSystematicStudyPresenter
import br.all.review.presenter.RestfulFindAllSystematicStudiesPresenter
import br.all.review.presenter.RestfulFindOneSystematicStudyPresenter
import br.all.review.presenter.RestfulUpdateSystematicStudyPresenter
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*
import br.all.application.review.create.CreateSystematicStudyService.RequestModel as CreateRequestModel
import br.all.application.review.update.services.UpdateSystematicStudyService.RequestModel as UpdateRequestModel

@RestController
@RequestMapping("/api/v1/researcher/{researcherId}/systematic-study")
class SystematicStudyController(
    private val createSystematicStudyService: CreateSystematicStudyService,
    private val findOneSystematicStudyServiceImpl: FindOneSystematicStudyService,
    private val findAllSystematicStudiesService: FindAllSystematicStudiesService,
    private val updateSystematicStudyService: UpdateSystematicStudyService,
) {
    @PostMapping
    @Operation(summary = "Create a systematic study")
    @ApiResponses(value = [
        ApiResponse(responseCode = "201", description = "Successful Operation"),
        ApiResponse(responseCode = "400", description = "Failed Operation for invalid systematic study"),
    ])
    fun postSystematicStudy(
        @PathVariable researcherId: UUID,
        @RequestBody request: CreateRequestModel
    ): ResponseEntity<*> {
        val presenter = RestfulCreateSystematicStudyPresenter()
        createSystematicStudyService.create(presenter, researcherId, request)
        return presenter.responseEntity ?: ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @GetMapping("/{systematicStudyId}")
    @Operation(summary = "Find a systematic study")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Successful Operation"),
        ApiResponse(responseCode = "404", description = "Failed Operation for nonexistent systematic study"),
        ApiResponse(responseCode = "403", description = "Failed Operation for not authorized researcher")
    ])
    fun findSystematicStudy(
        @PathVariable researcherId: UUID,
        @PathVariable systematicStudyId: UUID,
    ): ResponseEntity<*> {
        val presenter = RestfulFindOneSystematicStudyPresenter()
        findOneSystematicStudyServiceImpl.findById(presenter, researcherId, systematicStudyId)
        return presenter.responseEntity ?: ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @GetMapping
    @Operation(summary = "Find all systematic studies of a researcher")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Successful Operation"),
        ApiResponse(responseCode = "404", description = "Failed Operation for nonexistent systematic study"),
        ApiResponse(responseCode = "403", description = "Failed Operation for not authorized researcher")
    ])
    fun findAllSystematicStudies(@PathVariable researcherId: UUID): ResponseEntity<*> {
        val presenter = RestfulFindAllSystematicStudiesPresenter()
        findAllSystematicStudiesService.findAll(presenter, researcherId)
        return presenter.responseEntity ?: ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR)
    }
    
    @GetMapping("/owner/{ownerId}")
    @Operation(summary = "Find all systematic studies of a owner")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Successful Operation"),
        ApiResponse(responseCode = "404", description = "Failed Operation for nonexistent systematic study"),
        ApiResponse(responseCode = "403", description = "Failed Operation for not authorized owner")
    ])
    fun findAllSystematicStudiesByOwner(
        @PathVariable researcherId: UUID,
        @PathVariable ownerId: UUID,
    ): ResponseEntity<*> {
        val presenter = RestfulFindAllSystematicStudiesPresenter()
        findAllSystematicStudiesService.findAllByOwner(presenter, researcherId, ownerId)
        return presenter.responseEntity ?: ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @PutMapping("/{systematicStudyId}")
    @Operation(summary = "Update a systematic study")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Successful Operation"),
        ApiResponse(responseCode = "404", description = "Failed Operation for nonexistent systematic study"),
    ])
    fun updateSystematicStudy(
        @PathVariable researcherId: UUID,
        @PathVariable systematicStudyId: UUID,
        @RequestBody request: UpdateRequestModel,
    ): ResponseEntity<*> {
        val presenter = RestfulUpdateSystematicStudyPresenter()
        updateSystematicStudyService.update(presenter, researcherId, systematicStudyId, request)
        return presenter.responseEntity ?: ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR)
    }
}