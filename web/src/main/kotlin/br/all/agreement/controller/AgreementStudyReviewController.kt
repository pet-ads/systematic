package br.all.agreement.controller

import br.all.agreement.presenter.RestfulFindAgreementReviewsPresenter
import br.all.application.agreement.reviews.FindAgreementReviewsService
import br.all.application.study.agreement.AgreementStudyReviewService
import br.all.domain.model.study.StudyReviewStage
import br.all.security.service.AuthenticationInfoService
import br.all.study.presenter.RestfulBatchAnswerQuestionPresenter
import br.all.study.presenter.RestfulRemoveCriteriaPresenter
import br.all.study.presenter.RestfulUpdateStudyReviewStatusPresenter
import br.all.study.requests.PatchBatchAnswerQuestionStudyReviewRequest
import br.all.study.requests.PatchStatusStudyReviewRequest
import br.all.study.requests.RemoveCriteriaRequest
import br.all.utils.LinksFactory
import io.swagger.v3.oas.annotations.Operation
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/v1/systematic-study/{systematicStudy}/agreement/study-review")
class AgreementStudyReviewController(
    private val agreementStudyReviewService: AgreementStudyReviewService,
    private val authenticationInfoService: AuthenticationInfoService,
    private val linksFactory: LinksFactory,
    private val findAgreementReviewsService: FindAgreementReviewsService,
) {

    @PatchMapping("/selection-status")
    @Operation(summary = "Resolve the selection status of a study review in the agreement (owner or admin only)")
    fun updateSelectionStatus(
        @PathVariable systematicStudy: UUID,
        @RequestBody patchRequest: PatchStatusStudyReviewRequest
    ): ResponseEntity<*> {
        val presenter = RestfulUpdateStudyReviewStatusPresenter(linksFactory)
        val userId = authenticationInfoService.getAuthenticatedUserId()
        agreementStudyReviewService.changeSelectionStatus(presenter, patchRequest.toRequestModel(userId, systematicStudy))
        return presenter.responseEntity ?: ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @PatchMapping("/extraction-status")
    @Operation(summary = "Resolve the extraction status of a study review in the agreement (owner or admin only)")
    fun updateExtractionStatus(
        @PathVariable systematicStudy: UUID,
        @RequestBody patchRequest: PatchStatusStudyReviewRequest
    ): ResponseEntity<*> {
        val presenter = RestfulUpdateStudyReviewStatusPresenter(linksFactory)
        val userId = authenticationInfoService.getAuthenticatedUserId()
        agreementStudyReviewService.changeExtractionStatus(presenter, patchRequest.toRequestModel(userId, systematicStudy))
        return presenter.responseEntity ?: ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @PatchMapping("/reading-priority")
    @Operation(summary = "Update the reading priority of the original study review (owner or admin only)")
    fun updateReadingPriority(
        @PathVariable systematicStudy: UUID,
        @RequestBody patchRequest: PatchStatusStudyReviewRequest
    ): ResponseEntity<*> {
        val presenter = RestfulUpdateStudyReviewStatusPresenter(linksFactory)
        val userId = authenticationInfoService.getAuthenticatedUserId()
        agreementStudyReviewService.changeReadingPriority(presenter, patchRequest.toRequestModel(userId, systematicStudy))
        return presenter.responseEntity ?: ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @PatchMapping("/{studyReview}/batch-answer-question")
    @Operation(summary = "Answer extraction/RoB questions of the original study review (owner or admin only)")
    fun batchAnswerQuestions(
        @PathVariable systematicStudy: UUID,
        @PathVariable studyReview: Long,
        @RequestBody requestBody: PatchBatchAnswerQuestionStudyReviewRequest
    ): ResponseEntity<*> {
        val presenter = RestfulBatchAnswerQuestionPresenter(linksFactory)
        val userId = authenticationInfoService.getAuthenticatedUserId()
        agreementStudyReviewService.batchAnswerQuestion(
            presenter, requestBody.toRequestModel(userId, systematicStudy, studyReview)
        )
        return presenter.responseEntity ?: ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @PatchMapping("/remove-criteria/{studyReviewId}")
    @Operation(summary = "Remove criteria of the original study review (owner or admin only)")
    fun removeCriteria(
        @PathVariable systematicStudy: UUID,
        @PathVariable studyReviewId: Long,
        @RequestBody patchRequest: RemoveCriteriaRequest
    ): ResponseEntity<*> {
        val presenter = RestfulRemoveCriteriaPresenter(linksFactory)
        val userId = authenticationInfoService.getAuthenticatedUserId()
        agreementStudyReviewService.removeCriteria(
            presenter,
            patchRequest.toRequestModel(userId, systematicStudy, studyReviewId, patchRequest.stage)
        )
        return presenter.responseEntity ?: ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @GetMapping("/reviews")
    @Operation(summary = "Get the reviewers' classifications of studies in the agreement, according to the agreement visibility")
    fun findReviews(
        @PathVariable systematicStudy: UUID,
        @RequestParam("stage") stage: StudyReviewStage,
        @RequestParam("studyReviewIds") studyReviewIds: List<Long>,
    ): ResponseEntity<*> {
        val presenter = RestfulFindAgreementReviewsPresenter()
        val userId = authenticationInfoService.getAuthenticatedUserId()
        findAgreementReviewsService.findReviews(
            presenter, FindAgreementReviewsService.RequestModel(userId, systematicStudy, stage, studyReviewIds)
        )
        return presenter.responseEntity ?: ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR)
    }
}