package br.all.review.controller

import br.all.application.review.create.CreateSystematicStudyService
import br.all.review.presenter.RestfulCreateSystematicStudyPresenter
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*
import br.all.application.review.create.CreateSystematicStudyService.RequestModel as CreateRequestModel

@RestController
@RequestMapping("/researcher/{researcherId}/systematic-study")
class SystematicStudyController(
    private val createSystematicStudyService: CreateSystematicStudyService,
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

    @GetMapping("{systematicStudyId}")
    fun findSystematicStudy(
        @PathVariable researcherId: UUID,
        @PathVariable systematicStudyId: UUID,
    ): ResponseEntity<*> {
        TODO("Not yet implemented!")
    }
}