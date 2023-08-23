package br.all.controller

import br.all.application.study.create.CreateStudyReviewService
import br.all.application.study.create.StudyReviewRequestModel
import br.all.application.study.find.FindAllStudyReviewsService
import br.all.application.study.find.StudyReviewsResponseModel
import br.all.application.study.repository.StudyReviewDto
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/v1/review/{review}/study-review")
class StudyReviewController(val createService: CreateStudyReviewService, val findService: FindAllStudyReviewsService) {

    @GetMapping
    fun findAllStudyReviewsOf(@PathVariable review: UUID): ResponseEntity<StudyReviewsResponseModel> {
        val result = findService.findAllFromReview(review)
        return ResponseEntity(result, HttpStatus.OK)
    }


    @PostMapping
    fun createStudyReviewIn(@PathVariable review: UUID, @RequestBody request: StudyReviewRequestModel)
            : ResponseEntity<StudyReviewDto> {
        val result = createService.createFromStudy(review, request)
        return ResponseEntity(result, HttpStatus.CREATED)
    }

}