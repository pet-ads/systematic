package br.all.review.controller

import br.all.application.review.create.CreateSystematicStudyService
import br.all.application.review.find.services.FindAllSystematicStudiesService
import br.all.application.review.find.services.FindOneSystematicStudyService
import br.all.review.presenter.RestfulCreateSystematicStudyPresenter
import br.all.review.presenter.RestfulFindAllSystematicStudiesPresenter
import br.all.review.presenter.RestfulFindOneSystematicStudyPresenter
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*
import br.all.application.review.create.CreateSystematicStudyService.RequestModel as CreateRequestModel

@RestController
@RequestMapping("/api/v1/researcher/{researcherId}/systematic-study")
class SystematicStudyController(
    private val createSystematicStudyService: CreateSystematicStudyService,
    private val findOneSystematicStudyServiceImpl: FindOneSystematicStudyService,
    private val findAllSystematicStudiesService: FindAllSystematicStudiesService,
) {
    @PostMapping
    fun postSystematicStudy(
        @PathVariable researcherId: UUID,
        @RequestBody request: CreateRequestModel
    ): ResponseEntity<*> {
        val presenter = RestfulCreateSystematicStudyPresenter()
        createSystematicStudyService.create(presenter, researcherId, request)
        return presenter.responseEntity ?: ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR)
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
}