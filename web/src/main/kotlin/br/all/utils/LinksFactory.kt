package br.all.utils

import br.all.protocol.controller.ProtocolController
import br.all.protocol.requests.PutRequest
import br.all.question.controller.ExtractionQuestionController
import br.all.question.controller.RiskOfBiasQuestionController
import br.all.report.controller.ReportController
import br.all.review.controller.SystematicStudyController
import br.all.review.requests.PostRequest
import br.all.search.controller.SearchSessionController
import br.all.study.controller.StudyReviewController
import br.all.study.requests.PatchDuplicatedStudiesRequest
import br.all.study.requests.PatchStatusStudyReviewRequest
import br.all.study.requests.PostStudyReviewRequest
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

    fun findSession(systematicStudyId: UUID, sessionId: UUID): Link = linkTo<SearchSessionController> {
        findSearchSession(systematicStudyId, sessionId)
    }.withRel("find-session").withType("GET")

    fun findAllSessions(systematicStudyId: UUID): Link = linkTo<SearchSessionController> {
        findAllSearchSessions(
            systematicStudyId
        )
    }.withRel("find-all-sessions").withType("GET")

    fun findSessionsBySource(systematicStudyId: UUID, source:String): Link = linkTo<SearchSessionController> {
        findSearchSessionsBySource(systematicStudyId, source)
    }.withRel("find-sessions-by-source").withType("GET")

    fun updateSession(systematicStudyId: UUID, sessionId: UUID): Link = linkTo<SearchSessionController> {
        updateSearchSession(
            systematicStudyId,
            sessionId,
            SearchSessionController.PutRequest("searchString",
                "additionalInfo", "source")
        )
    }.withRel("update-session").withType("PUT")

    fun createStudy(systematicStudyId: UUID): Link = linkTo<StudyReviewController> {
        createStudyReview(
            systematicStudyId,
            PostStudyReviewRequest(
                searchSessionId = UUID.randomUUID(),
                type = "",
                title = "",
                year = 2024,
                authors = "",
                venue = "",
                abstract = "",
                keywords = emptySet(),
                source = ""
            )
        )
    }.withRel("create-study").withType("POST")

    fun findStudy(systematicStudyId: UUID, studyId: Long): Link = linkTo<StudyReviewController> {
        findStudyReview(systematicStudyId, studyId)
    }.withRel("find-study").withType("GET")

    fun findAllStudies(systematicStudyId: UUID): Link = linkTo<StudyReviewController> {
        findAllStudyReviews(systematicStudyId)
    }.withRel("find-all-studies").withType("GET")

    fun findAllStudiesBySource(systematicStudyId: UUID, source: String): Link = linkTo<StudyReviewController> {
        findAllStudyReviewsBySource(systematicStudyId, source)
    }.withRel("find-all-studies-by-source").withType("GET")

    fun findAllStudiesBySession(systematicStudyId: UUID, searchSessionId: UUID): Link = linkTo<StudyReviewController> {
        findAllStudyReviewsBySession(systematicStudyId, searchSessionId)
    }.withRel("find-all-studies-by-session").withType("GET")

    fun updateStudySelectionStatus(systematicStudyId: UUID): Link = linkTo<StudyReviewController> {
        updateStudyReviewSelectionStatus(
            systematicStudyId,
            patchRequest = PatchStatusStudyReviewRequest(
                listOf(111, 112),
                "status",
                setOf("criteria")
            )
        )
    }.withRel("update-study-selection-status").withType("PATCH")

    fun updateStudyExtractionStatus(systematicStudyId: UUID): Link = linkTo<StudyReviewController> {
        updateStudyReviewExtractionStatus(
            systematicStudyId,
            patchRequest = PatchStatusStudyReviewRequest(
                listOf(111, 112),
                "status",
                setOf("criteria")
            )
        )
    }.withRel("update-study-extraction-status").withType("PATCH")

    fun updateStudyReadingPriority(systematicStudyId: UUID): Link = linkTo<StudyReviewController> {
        updateStudyReviewReadingPriority(
            systematicStudyId,
            patchRequest = PatchStatusStudyReviewRequest(
                listOf(111, 112),
                "status",
                setOf("criteria")
            )
        )
    }.withRel("update-study-reading-priority").withType("PATCH")

    fun markStudyAsDuplicated(systematicStudyId: UUID): Link =
        linkTo<StudyReviewController> {
            markAsDuplicated(
                systematicStudyId,
                referenceStudyId = 11111,
                duplicatedRequest = PatchDuplicatedStudiesRequest(
                    duplicatedStudyIds = listOf(222222)
                )
            )
        }.withRel("mark-studies-as-duplicated").withType("PATCH")

    fun findAnswers(systematicStudyId: UUID, questionId: UUID, studyId: Long): Link =
        linkTo<ReportController> {
            findAnswers(
                systematicStudyId,
                studyReviewId = studyId,
                questionId = questionId
            )
        }.withRel("find-answers").withType("GET")

    fun findCriteria(systematicStudyId: UUID, type: String, studyId: Long): Link =
        linkTo<ReportController> {
            findCriteria(
                systematicStudyId,
                studyId,
                type
            )
        }.withRel("find-criteria").withType("GET")

    fun findSource(systematicStudyId: UUID, studyId: Long, source: String): Link =
        linkTo<ReportController> {
            findSource(
                systematicStudyId,
                studyId,
                source
            )
        }.withRel("find-source").withType("GET")

    fun authorNetwork(systematicStudyId: UUID, studyId: Long): Link =
        linkTo<ReportController> {
            authorNetwork(
                systematicStudyId,
                studyId
            )
        }.withRel("author-network").withType("GET")

    fun keywords(systematicStudyId: UUID, studyId: Long, filter: String?): Link =
        linkTo<ReportController> {
            findKeywords(
                systematicStudyId,
                studyId,
                filter
            )
        }.withRel("find-keywords").withType("GET")

    fun exportProtocol(systematicStudyId: UUID): Link =
        linkTo<ReportController> {
            exportProtocol(
                systematicStudyId,
            )
        }.withRel("exportable-protocol").withType("GET")

    fun findStudiesByStage(systematicStudyId: UUID, stage: String): Link =
        linkTo<ReportController> {
            findStudiesByStage(
                systematicStudyId,
                stage
            )
        }.withRel("find-studies-stage").withType("GET")

    fun studiesFunnel(systematicStudyId: UUID): Link =
        linkTo<ReportController> {
            studiesFunnel(
                systematicStudyId,
            )
        }.withRel("studies-funnel").withType("GET")
}

