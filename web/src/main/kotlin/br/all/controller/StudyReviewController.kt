package br.all.controller

import br.all.application.study.create.CreateStudyReviewPresenter
import br.all.application.study.create.CreateStudyReviewService
import br.all.application.study.find.FindAllStudyReviewsService
import br.all.application.study.find.FindStudyReviewService
import br.all.presenter.RestCreateStudyReviewPresenter
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import br.all.presenter.RestCreateStudyReviewPresenter.ViewModel as CreateViewModel
import br.all.application.study.find.FindStudyReviewService.RequestModel as FindOneRequest
import br.all.application.study.find.FindStudyReviewService.ResponseModel as FindOneResponse
import br.all.application.study.create.CreateStudyReviewService.RequestModel as CreateRequest
import br.all.application.study.find.FindAllStudyReviewsService.ResponseModel as FindAllResponse
import br.all.application.study.find.FindAllStudyReviewsService.RequestModel as FindAllRequest
import java.util.UUID

@RestController
@RequestMapping("/api/v1/review/{review}/study-review")
class StudyReviewController(
    val createService: CreateStudyReviewService,
    val createPresenter: CreateStudyReviewPresenter,
    val findAllService: FindAllStudyReviewsService,
    val findOneService: FindStudyReviewService
) {

    @PostMapping
    fun createStudyReview(@PathVariable review: UUID, @RequestBody request: CreateRequest): ResponseEntity<CreateViewModel> {
        createService.createFromStudy(review, request)
        val restPresenter = createPresenter as RestCreateStudyReviewPresenter
        return restPresenter.responseEntity
    }

    @GetMapping
    fun findAllStudyReviews(@PathVariable review: UUID): ResponseEntity<FindAllResponse> {
        val result = findAllService.findAllFromReview(FindAllRequest(review))
        return ResponseEntity.ok(result)
    }

    @GetMapping("/{study}")
    fun findStudyReview(@PathVariable review: UUID, @PathVariable study: Long): ResponseEntity<FindOneResponse> {
        val result = findOneService.findOne(FindOneRequest(review, study))
        return ResponseEntity.ok(result)
    }
}