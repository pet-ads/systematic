package br.all.utils

import br.all.protocol.controller.ProtocolController
import br.all.protocol.requests.PutRequest
import br.all.question.controller.ExtractionQuestionController
import br.all.question.controller.RiskOfBiasQuestionController
import br.all.review.controller.SystematicStudyController
import br.all.review.requests.PostRequest
import org.springframework.hateoas.Link
import org.springframework.hateoas.server.mvc.linkTo
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class LinksFactory {

    fun findProtocol(systematicStudyId: UUID): Link =
        linkTo<ProtocolController> { findById(systematicStudyId) }.withRel("find-protocol").withType("GET")

    fun updateProtocol(systematicStudyId: UUID): Link =
        linkTo<ProtocolController> {
            putProtocol(
                systematicStudyId,
                request = PutRequest()
            )
        }.withRel("update-protocol").withType("PUT")

    fun createReview(): Link = linkTo<SystematicStudyController> {
        postSystematicStudy(PostRequest("title", "description", setOf()))
    }.withRel("create-review").withType("POST")

    fun findReview(systematicStudyId: UUID): Link = linkTo<SystematicStudyController> {
        findSystematicStudy(systematicStudyId)
    }.withRel("find-review").withType("GET")

    fun findMyReviews(ownerId: UUID): Link = linkTo<SystematicStudyController> {
        findAllSystematicStudiesByOwner(ownerId)
    }.withRel("find-my-reviews").withType("GET")

    fun findAllReviews(): Link = linkTo<SystematicStudyController> {
        findAllSystematicStudies()
    }.withRel("find-all-reviews").withType("GET")

    fun updateReview(systematicStudyId: UUID): Link = linkTo<SystematicStudyController> {
        updateSystematicStudy(
            systematicStudyId,
            br.all.review.requests.PutRequest("title", "description"),
        )
    }.withRel("update-review").withType("PUT")

    fun findExtractionQuestion(systematicStudyId: UUID, questionId: UUID): Link = linkTo<ExtractionQuestionController> {
        findQuestion(systematicStudyId, questionId)
    }.withRel("find-extraction-question").withType("GET")

    fun findAllReviewExtractionQuestions(systematicStudyId: UUID): Link = linkTo<ExtractionQuestionController> {
        findAllBySystematicStudyId(systematicStudyId)
    }.withRel("find-all-review-extraction-questions").withType("GET")

    fun createTextualExtractionQuestion(systematicStudyId: UUID): Link = linkTo<ExtractionQuestionController> {
        createTextualQuestion(
            systematicStudyId,
            request = ExtractionQuestionController.TextualRequest(
                "code", "description"
            )
        )
    }.withRel("create-textual-extraction-question").withType("POST")

    fun createPickListExtractionQuestion(systematicStudyId: UUID): Link = linkTo<ExtractionQuestionController> {
        createPickListQuestion(
            systematicStudyId,
            request = ExtractionQuestionController.PickListRequest(
                "code", "description", listOf("option1")
            )
        )
    }.withRel("create-pick-list-extraction-question").withType("POST")

    fun createLabeledScaleExtractionQuestion(systematicStudyId: UUID): Link = linkTo<ExtractionQuestionController> {
        createLabeledScaleQuestion(
            systematicStudyId,
            request = ExtractionQuestionController.LabeledScaleRequest(
                "code", "description", mapOf("scale1" to 1)
            )
        )
    }.withRel("create-labeled-scale-extraction-question").withType("POST")

    fun createNumberScaleExtractionQuestion(systematicStudyId: UUID): Link = linkTo<ExtractionQuestionController> {
        createNumberScaleQuestion(
            systematicStudyId,
            request = ExtractionQuestionController.NumberScaleRequest(
                "code", "description", 0, 0
            )
        )
    }.withRel("create-numberScale-extraction-question").withType("POST")

    fun findRobQuestion(systematicStudyId: UUID, questionId: UUID): Link = linkTo<RiskOfBiasQuestionController> {
        findQuestion(systematicStudyId, questionId)
    }.withRel("find-rob-question").withType("GET")

    fun findAllReviewRobQuestions(systematicStudyId: UUID): Link = linkTo<RiskOfBiasQuestionController> {
        findAllBySystematicStudyId(systematicStudyId)
    }.withRel("find-all-review-rob-questions").withType("GET")

    fun createTextualRobQuestion(systematicStudyId: UUID): Link = linkTo<RiskOfBiasQuestionController> {
        createTextualQuestion(
            systematicStudyId,
            request = RiskOfBiasQuestionController.TextualRequest(
                "code", "description"
            )
        )
    }.withRel("create-textual-rob-question").withType("POST")


    fun createPickListRobQuestion(systematicStudyId: UUID): Link = linkTo<RiskOfBiasQuestionController> {
        createPickListQuestion(
            systematicStudyId,
            request = RiskOfBiasQuestionController.PickListRequest(
                "code", "description", listOf("option1")
            )
        )
    }.withRel("create-pick-list-rob-question").withType("POST")

    fun createLabeledScaleRobQuestion(systematicStudyId: UUID): Link = linkTo<RiskOfBiasQuestionController> {
        createLabeledScaleQuestion(
            systematicStudyId,
            request = RiskOfBiasQuestionController.LabeledScaleRequest(
                "code", "description", mapOf("scale1" to 1)
            )
        )
    }.withRel("create-labeled-scale-rob-question").withType("POST")

    fun createNumberScaleRobQuestion(systematicStudyId: UUID): Link = linkTo<RiskOfBiasQuestionController> {
        createNumberScaleQuestion(
            systematicStudyId,
            request = RiskOfBiasQuestionController.NumberScaleRequest(
                "code", "description", 0, 0
            )
        )
    }.withRel("create-numberScale-rob-question").withType("POST")
}

