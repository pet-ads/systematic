package br.all.study.controller

import br.all.application.study.create.CreateStudyReviewPresenter
import br.all.application.study.create.CreateStudyReviewService
import br.all.application.study.find.presenter.FindAllStudyReviewsPresenter
import br.all.application.study.find.presenter.FindStudyReviewPresenter
import br.all.application.study.find.service.FindAllStudyReviewsService
import br.all.application.study.find.service.FindStudyReviewService
import br.all.application.study.update.implementation.UpdateStudyReviewExtractionService
import br.all.application.study.update.implementation.UpdateStudyReviewPriorityService
import br.all.application.study.update.implementation.UpdateStudyReviewSelectionService
import br.all.application.study.update.interfaces.UpdateStudyReviewStatusPresenter
import br.all.study.presenter.RestfulCreateStudyReviewPresenter
import br.all.study.presenter.RestfulFindStudyReviewPresenter
import br.all.study.presenter.RestfulFindAllStudyReviewsPresenter
import br.all.study.presenter.RestfulUpdateStudyReviewStatusPresenter
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*
import br.all.application.study.create.CreateStudyReviewService.RequestModel as CreateRequest
import br.all.application.study.find.service.FindAllStudyReviewsService.RequestModel as FindAllRequest
import br.all.application.study.find.service.FindStudyReviewService.RequestModel as FindOneRequest
import br.all.application.study.update.interfaces.UpdateStudyReviewStatusService.RequestModel as UpdateStatusRequest

@RestController
@RequestMapping("/api/v1/researcher/{researcher}/review/{review}/study-review")
class StudyReviewController(
    val createService: CreateStudyReviewService,
    val findAllService: FindAllStudyReviewsService,
    val findOneService: FindStudyReviewService,
    val updateSelectionService: UpdateStudyReviewSelectionService,
    val updateExtractionService: UpdateStudyReviewExtractionService,
    val updateReadingPriorityService: UpdateStudyReviewPriorityService,
) {

    @PostMapping
    fun createStudyReview(
        @PathVariable researcher: UUID,
        @PathVariable review: UUID,
        @RequestBody request: CreateRequest
    ): ResponseEntity<*> {
        val presenter = RestfulCreateStudyReviewPresenter()
        createService.createFromStudy(presenter, request)
        return presenter.responseEntity?: ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @GetMapping
    fun findAllStudyReviews(
        @PathVariable researcher: UUID,
        @PathVariable review: UUID,
    ): ResponseEntity<*> {
        val presenter =  RestfulFindAllStudyReviewsPresenter()
        val request = FindAllRequest(researcher, review)
        findAllService.findAllFromReview(presenter, request)
        return presenter.responseEntity?: ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @GetMapping("/{study}")
    fun findStudyReview(
        @PathVariable researcher: UUID,
        @PathVariable review: UUID,
        @PathVariable study: Long,
    ): ResponseEntity<*> {
        val presenter = RestfulFindStudyReviewPresenter()
        val request = FindOneRequest(researcher, review, study)
        findOneService.findOne(presenter, request)
        return presenter.responseEntity?: ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @PatchMapping("/{study}/selection-status")
    fun updateStudyReviewSelectionStatus(
        @PathVariable researcher: UUID,
        @PathVariable review: UUID,
        @PathVariable study: Long,
        @RequestBody request: UpdateStatusRequest
    ): ResponseEntity<*> {
        val presenter = RestfulUpdateStudyReviewStatusPresenter()
        updateSelectionService.changeStatus(presenter, request)
        return presenter.responseEntity?: ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @PatchMapping("/{study}/extraction-status")
    fun updateStudyReviewExtractionStatus(
        @PathVariable researcher: UUID,
        @PathVariable review: UUID,
        @PathVariable study: Long,
        @RequestBody request: UpdateStatusRequest
    ): ResponseEntity<*> {
        val presenter = RestfulUpdateStudyReviewStatusPresenter()
        updateExtractionService.changeStatus(presenter, request)
        return presenter.responseEntity?: ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @PatchMapping("/{study}/reading-priority")
    fun updateStudyReviewReadingPriority(
        @PathVariable researcher: UUID,
        @PathVariable review: UUID,
        @PathVariable study: Long,
        @RequestBody request: UpdateStatusRequest
    ): ResponseEntity<*> {
        val presenter = RestfulUpdateStudyReviewStatusPresenter()
        updateReadingPriorityService.changeStatus(presenter, request)
        return presenter.responseEntity?: ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR)
    }
}