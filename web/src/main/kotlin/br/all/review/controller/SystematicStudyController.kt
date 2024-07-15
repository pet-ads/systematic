package br.all.review.controller

import br.all.application.review.create.CreateSystematicStudyService
import br.all.application.review.find.services.FindAllSystematicStudiesService
import br.all.application.review.find.services.FindAllSystematicStudiesService.FindByOwnerRequest
import br.all.application.review.find.services.FindSystematicStudyService
import br.all.application.review.update.services.UpdateSystematicStudyService
import br.all.review.presenter.RestfulCreateSystematicStudyPresenter
import br.all.review.presenter.RestfulFindAllSystematicStudiesPresenter
import br.all.review.presenter.RestfulFindSystematicStudyPresenter
import br.all.review.presenter.RestfulUpdateSystematicStudyPresenter
import br.all.review.requests.PostRequest
import br.all.review.requests.PutRequest
import br.all.security.service.AuthenticationInfoService
import br.all.utils.LinksFactory
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*
import br.all.application.review.find.services.FindSystematicStudyService.RequestModel as FindOneRequestModel

@RestController
@RequestMapping("/api/v1/systematic-study")
class SystematicStudyController(
    private val createSystematicStudyService: CreateSystematicStudyService,
    private val findSystematicStudyServiceImpl: FindSystematicStudyService,
    private val findAllSystematicStudiesService: FindAllSystematicStudiesService,
    private val updateSystematicStudyService: UpdateSystematicStudyService,
    private val authenticationInfoService: AuthenticationInfoService,
    private val linksFactory: LinksFactory
) {

    @PostMapping
    @Operation(summary = "Create a systematic study")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "201", description = "Success creating a systematic study",
                content = [Content(schema = Schema(hidden = true))]),
            ApiResponse(
                responseCode = "400",
                description = "Fail creating a systematic study - invalid systematic study",
                content = [Content(schema = Schema(hidden = true))]
            ),
            ApiResponse(
                responseCode = "401",
                description = "Fail creating a systematic study - unauthenticated user",
                content = [Content(schema = Schema(hidden = true))]
            ),
            ApiResponse(
                responseCode = "403",
                description = "Fail creating a systematic study - unauthorized user",
                content = [Content(schema = Schema(hidden = true))]
            ),
        ]
    )
    fun postSystematicStudy(@RequestBody request: PostRequest): ResponseEntity<*> {
        val presenter = RestfulCreateSystematicStudyPresenter(linksFactory)
        val userId = authenticationInfoService.getAuthenticatedUserId()
        val requestModel = request.toCreateRequestModel(userId)

        createSystematicStudyService.create(presenter, requestModel)
        return presenter.responseEntity ?: ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @GetMapping("/{systematicStudyId}")
    @Operation(summary = "Get a systematic study by id")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200", description = "Success getting a systematic study by id",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = FindSystematicStudyService.ResponseModel::class)
                )]
            ),
            ApiResponse(
                responseCode = "401",
                description = "Fail creating a systematic study - unauthenticated user",
                content = [Content(schema = Schema(hidden = true))]
            ),
            ApiResponse(
                responseCode = "403",
                description = "Fail getting systematic study - unauthorized user",
                content = [Content(schema = Schema(hidden = true))]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Fail getting systematic study - not found",
                content = [Content(schema = Schema(hidden = true))]
            ),
        ]
    )
    fun findSystematicStudy(@PathVariable systematicStudyId: UUID): ResponseEntity<*> {
        val presenter = RestfulFindSystematicStudyPresenter(linksFactory)
        val userId = authenticationInfoService.getAuthenticatedUserId()
        val request = FindOneRequestModel(userId, systematicStudyId)

        findSystematicStudyServiceImpl.findById(presenter, request)
        return presenter.responseEntity ?: ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @GetMapping
    @Operation(summary = "Get all systematic studies of a given reviewer")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Success getting all systematic studies of a given reviewer. Either all systematic studies or none",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = FindAllSystematicStudiesService.ResponseModel::class)
                )]
            ),
            ApiResponse(
                responseCode = "401",
                description = "Fail getting all systematic studies of a given reviewer - unauthenticated user",
                content = [Content(schema = Schema(hidden = true))]
            ),
            ApiResponse(
                responseCode = "403",
                description = "Fail getting all systematic studies of a given reviewer - unauthorized user",
                content = [Content(schema = Schema(hidden = true))]
            )
        ]
    )
    fun findAllSystematicStudies(): ResponseEntity<*> {
        val presenter = RestfulFindAllSystematicStudiesPresenter(linksFactory)
        val userId = authenticationInfoService.getAuthenticatedUserId()

        findAllSystematicStudiesService.findAllByCollaborator(presenter, userId)
        return presenter.responseEntity ?: ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @GetMapping("/owner/{ownerId}")
    @Operation(summary = "Get all systematic studies of a given owner")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Success getting all systematic studies of a given owner. Either all systematic studies or none",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = FindAllSystematicStudiesService.ResponseModel::class)
                )]
            ),
            ApiResponse(
                responseCode = "401",
                description = "Fail getting all systematic studies of a given owner - unauthenticated user",
                content = [Content(schema = Schema(hidden = true))]
            ),
            ApiResponse(
                responseCode = "403",
                description = "Fail getting all systematic studies of a given owner - unauthorized user",
                content = [Content(schema = Schema(hidden = true))]
            )
        ]
    )
    fun findAllSystematicStudiesByOwner(@PathVariable ownerId: UUID): ResponseEntity<*> {
        val presenter = RestfulFindAllSystematicStudiesPresenter(linksFactory)
        val userId = authenticationInfoService.getAuthenticatedUserId()
        val request = FindByOwnerRequest(userId, ownerId)

        findAllSystematicStudiesService.findAllByOwner(presenter, request)
        return presenter.responseEntity ?: ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @PutMapping("/{systematicStudyId}")
    @Operation(summary = "Update an existing systematic study")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Success updating an existing systematic study",
                content = [Content(schema = Schema(hidden = true))]),
            ApiResponse(
                responseCode = "400",
                description = "Fail updating an existing systematic study - invalid systematic study",
                content = [Content(schema = Schema(hidden = true))]
            ),
            ApiResponse(
                responseCode = "401",
                description = "Fail updating an existing systematic study - unauthenticated user",
                content = [Content(schema = Schema(hidden = true))]
            ),
            ApiResponse(
                responseCode = "403",
                description = "Fail updating an existing systematic study - unauthorized user",
                content = [Content(schema = Schema(hidden = true))]
            ),
            ApiResponse(responseCode = "404", description = "Fail updating an existing systematic study - not found",
                content = [Content(schema = Schema(hidden = true))]),
        ]
    )
    fun updateSystematicStudy(@PathVariable systematicStudyId: UUID,
                              @RequestBody request: PutRequest): ResponseEntity<*> {
        val presenter = RestfulUpdateSystematicStudyPresenter(linksFactory)
        val userId = authenticationInfoService.getAuthenticatedUserId()
        val requestModel = request.toUpdateRequestModel(userId, systematicStudyId)

        updateSystematicStudyService.update(presenter, requestModel)
        return presenter.responseEntity ?: ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR)
    }
}