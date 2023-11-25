package br.all.study.controller

import br.all.application.study.create.CreateStudyReviewPresenter
import br.all.application.study.create.CreateStudyReviewService
import br.all.application.study.find.presenter.FindAllStudyReviewsPresenter
import br.all.application.study.find.presenter.FindStudyReviewPresenter
import br.all.application.study.find.service.FindAllStudyReviewsService
import br.all.application.study.find.service.FindStudyReviewService
import br.all.study.presenter.RestfulCreateStudyReviewPresenter
import br.all.study.presenter.RestfulFindStudyReviewPresenter
import br.all.study.presenter.RestfulFindAllStudyReviewsPresenter
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*
import br.all.application.study.create.CreateStudyReviewService.RequestModel as CreateRequest
import br.all.application.study.find.service.FindAllStudyReviewsService.RequestModel as FindAllRequest
import br.all.application.study.find.service.FindStudyReviewService.RequestModel as FindOneRequest

@RestController
@RequestMapping("/api/v1/researcher/{researcher}/review/{review}/study-review")
class StudyReviewController(
    val createService: CreateStudyReviewService,
    val createPresenter: CreateStudyReviewPresenter,
    val findAllService: FindAllStudyReviewsService,
    val findAllPresenter: FindAllStudyReviewsPresenter,
    val findOneService: FindStudyReviewService,
    val findOnePresenter: FindStudyReviewPresenter,
) {

    @PostMapping
    fun createStudyReview(
        @PathVariable researcher: UUID,
        @PathVariable review: UUID,
        @RequestBody request: CreateRequest
    ): ResponseEntity<*> {
        createService.createFromStudy(request)
        val restPresenter = createPresenter as RestfulCreateStudyReviewPresenter
        return restPresenter.responseEntity
    }

    @GetMapping
    fun findAllStudyReviews(
        @PathVariable researcher: UUID,
        @PathVariable review: UUID,
    ): ResponseEntity<*> {
        findAllService.findAllFromReview(FindAllRequest(researcher, review))
        val restPresenter = findAllPresenter as RestfulFindAllStudyReviewsPresenter
        return restPresenter.responseEntity
    }

    @GetMapping("/{study}")
    fun findStudyReview(
        @PathVariable researcher: UUID,
        @PathVariable review: UUID,
        @PathVariable study: Long,
    ): ResponseEntity<*> {
        findOneService.findOne(FindOneRequest(researcher, review, study))
        val restPresenter = findOnePresenter as RestfulFindStudyReviewPresenter
        return restPresenter.responseEntity
    }
}