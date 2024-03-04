package br.all.study.controller

import br.all.application.study.create.CreateStudyReviewService
import br.all.application.study.find.service.FindAllStudyReviewsBySourceService
import br.all.application.study.find.service.FindAllStudyReviewsService
import br.all.application.study.find.service.FindStudyReviewService
import br.all.application.study.update.implementation.UpdateStudyReviewExtractionService
import br.all.application.study.update.implementation.UpdateStudyReviewPriorityService
import br.all.application.study.update.implementation.UpdateStudyReviewSelectionService
import br.all.application.study.update.interfaces.MarkAsDuplicatedService
import br.all.study.presenter.*
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*
import br.all.application.study.update.interfaces.MarkAsDuplicatedService.RequestModel as DuplicatedRequest
import br.all.application.study.create.CreateStudyReviewService.RequestModel as CreateRequest
import br.all.application.study.find.service.FindAllStudyReviewsService.RequestModel as FindAllRequest
import br.all.application.study.find.service.FindAllStudyReviewsBySourceService.RequestModel as FindAllBySourceRequest
import br.all.application.study.find.service.FindStudyReviewService.RequestModel as FindOneRequest
import br.all.application.study.update.interfaces.UpdateStudyReviewStatusService.RequestModel as UpdateStatusRequest

@RestController
@RequestMapping("/api/v1/researcher/{researcher}/systematic-study/{systematicStudy}")
class StudyReviewController(
    val createService: CreateStudyReviewService,
    val findAllService: FindAllStudyReviewsService,
    val findAllBySourceService: FindAllStudyReviewsBySourceService,
    val findOneService: FindStudyReviewService,
    val updateSelectionService: UpdateStudyReviewSelectionService,
    val updateExtractionService: UpdateStudyReviewExtractionService,
    val updateReadingPriorityService: UpdateStudyReviewPriorityService,
    val markAsDuplicatedService: MarkAsDuplicatedService
) {

    @PostMapping("/study-review")
    fun createStudyReview(
        @PathVariable researcher: UUID,
        @PathVariable systematicStudy: UUID,
        @RequestBody request: CreateRequest
    ): ResponseEntity<*> {
        val presenter = RestfulCreateStudyReviewPresenter()
        createService.createFromStudy(presenter, request)
        return presenter.responseEntity?: ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @GetMapping("/study-review")
    fun findAllStudyReviews(
        @PathVariable researcher: UUID,
        @PathVariable systematicStudy: UUID,
    ): ResponseEntity<*> {
        val presenter =  RestfulFindAllStudyReviewsPresenter()
        val request = FindAllRequest(researcher, systematicStudy)
        findAllService.findAllFromReview(presenter, request)
        return presenter.responseEntity?: ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @GetMapping("/search-source/{searchSource}")
    fun findAllStudyReviewsBySource(
        @PathVariable researcher: UUID,
        @PathVariable systematicStudy: UUID,
        @PathVariable searchSource: String,
    ): ResponseEntity<*> {
        val presenter =  RestfulFindAllStudyReviewsBySourcePresenter()
        val request = FindAllBySourceRequest(researcher, systematicStudy, searchSource)
        findAllBySourceService.findAllFromSearchSession(presenter, request)
        return presenter.responseEntity?: ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @GetMapping("/study-review/{studyReview}")
    fun findStudyReview(
        @PathVariable researcher: UUID,
        @PathVariable systematicStudy: UUID,
        @PathVariable studyReview: Long,
    ): ResponseEntity<*> {
        val presenter = RestfulFindStudyReviewPresenter()
        val request = FindOneRequest(researcher, systematicStudy, studyReview)
        findOneService.findOne(presenter, request)
        return presenter.responseEntity?: ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @PatchMapping("/study-review/{studyReview}/selection-status")
    fun updateStudyReviewSelectionStatus(
        @PathVariable researcher: UUID,
        @PathVariable systematicStudy: UUID,
        @PathVariable studyReview: Long,
        @RequestBody request: UpdateStatusRequest
    ): ResponseEntity<*> {
        val presenter = RestfulUpdateStudyReviewStatusPresenter()
        updateSelectionService.changeStatus(presenter, request)
        return presenter.responseEntity?: ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @PatchMapping("/study-review/{studyReview}/extraction-status")
    fun updateStudyReviewExtractionStatus(
        @PathVariable researcher: UUID,
        @PathVariable systematicStudy: UUID,
        @PathVariable studyReview: Long,
        @RequestBody request: UpdateStatusRequest
    ): ResponseEntity<*> {
        val presenter = RestfulUpdateStudyReviewStatusPresenter()
        updateExtractionService.changeStatus(presenter, request)
        return presenter.responseEntity?: ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @PatchMapping("/study-review/{studyReview}/reading-priority")
    fun updateStudyReviewReadingPriority(
        @PathVariable researcher: UUID,
        @PathVariable systematicStudy: UUID,
        @PathVariable studyReview: Long,
        @RequestBody request: UpdateStatusRequest
    ): ResponseEntity<*> {
        val presenter = RestfulUpdateStudyReviewStatusPresenter()
        updateReadingPriorityService.changeStatus(presenter, request)
        return presenter.responseEntity?: ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @PatchMapping("/study-review/{studyReviewIdToKeep}/duplicated/{studyReviewToMarkAsDuplicated}")
    fun markAsDuplicated(
        @PathVariable researcher: UUID,
        @PathVariable systematicStudy: UUID,
        @PathVariable studyReviewIdToKeep: Long,
        @PathVariable studyReviewToMarkAsDuplicated: Long,
    ): ResponseEntity<*> {
        val presenter = RestfulMarkAsDuplicatedPresenter()
        val request = DuplicatedRequest(researcher, systematicStudy, studyReviewIdToKeep, studyReviewToMarkAsDuplicated)
        markAsDuplicatedService.markAsDuplicated(presenter, request)
        return presenter.responseEntity?: ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR)
    }
}