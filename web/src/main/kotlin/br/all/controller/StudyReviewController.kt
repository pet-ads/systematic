package br.all.controller

import br.all.modules.application.study.create.CreateStudyReviewService
import br.all.modules.application.study.create.StudyRequestModel
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/study-review")
class StudyReviewController(val service: CreateStudyReviewService) {

    @PostMapping
    fun createStudyReview(@RequestBody request: StudyRequestModel) {
        service.createFromStudy(request)
    }
}