package br.all.review.controller

import br.all.application.review.create.CreateSystematicStudyService
import br.all.application.review.find.services.FindAllSystematicStudiesService
import br.all.application.review.find.services.FindOneSystematicStudyService
import br.all.application.review.update.services.UpdateSystematicStudyService
import br.all.review.presenter.RestfulCreateSystematicStudyPresenter
import br.all.review.presenter.RestfulFindAllSystematicStudiesPresenter
import br.all.review.presenter.RestfulFindOneSystematicStudyPresenter
import br.all.review.presenter.RestfulUpdateSystematicStudyPresenter
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
    fun postSystematicStudy(
        @PathVariable researcherId: UUID,
        @RequestBody request: PostRequest,
    ): ResponseEntity<*> {
        val presenter = RestfulCreateSystematicStudyPresenter()
        val requestModel = request.toCreateRequestModel(researcherId)

        createSystematicStudyService.create(presenter, requestModel)
        return presenter.responseEntity ?: ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR)
    }

    data class PostRequest(
        val title: String,
        val description: String,
        val collaborators: Set<UUID>,
    ) {
        fun toCreateRequestModel(researcherId: UUID) = CreateRequestModel(researcherId, title, description, collaborators)
    }

    @GetMapping("/{systematicStudyId}")
    fun findSystematicStudy(
        @PathVariable researcherId: UUID,
        @PathVariable systematicStudyId: UUID,
    ): ResponseEntity<*> {
        val presenter = RestfulFindOneSystematicStudyPresenter()
        findOneSystematicStudyServiceImpl.findById(presenter, researcherId, systematicStudyId)
        return presenter.responseEntity ?: ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @GetMapping
    fun findAllSystematicStudies(@PathVariable researcherId: UUID): ResponseEntity<*> {
        val presenter = RestfulFindAllSystematicStudiesPresenter()
        findAllSystematicStudiesService.findAll(presenter, researcherId)
        return presenter.responseEntity ?: ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR)
    }
    
    @GetMapping("/owner/{ownerId}")
    fun findAllSystematicStudiesByOwner(
        @PathVariable researcherId: UUID,
        @PathVariable ownerId: UUID,
    ): ResponseEntity<*> {
        val presenter = RestfulFindAllSystematicStudiesPresenter()
        findAllSystematicStudiesService.findAllByOwner(presenter, researcherId, ownerId)
        return presenter.responseEntity ?: ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @PutMapping("/{systematicStudyId}")
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