package br.all.agreement.controller

import br.all.domain.model.agreement.AgreementVisibility
import br.all.domain.model.study.StudyReviewStage
import br.all.infrastructure.agreement.AgreementVisibilityDocument
import br.all.infrastructure.agreement.MongoAgreementVisibilityRepository
import br.all.infrastructure.collaborator.CollaboratorDocument
import br.all.infrastructure.collaborator.MongoCollaboratorRepository
import br.all.infrastructure.review.MongoSystematicStudyRepository
import br.all.infrastructure.reviewmode.MongoReviewerStudyRepository
import br.all.infrastructure.reviewmode.ReviewerStudyDocument
import br.all.security.service.ApplicationUser
import br.all.shared.TestHelperService
import br.all.study.utils.TestDataFactory
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.*
import br.all.review.shared.TestDataFactory as SystematicStudyTestDataFactory

@SpringBootTest
@AutoConfigureMockMvc
@Tag("IntegrationTest")
@Tag("ControllerTest")
class AgreementReviewsControllerTest @Autowired constructor(
    val reviewerStudyRepository: MongoReviewerStudyRepository,
    val visibilityRepository: MongoAgreementVisibilityRepository,
    val systematicStudyRepository: MongoSystematicStudyRepository,
    val collaboratorRepository: MongoCollaboratorRepository,
    private val testHelperService: TestHelperService,
    val mockMvc: MockMvc,
) {

    private val systematicStudyFactory = SystematicStudyTestDataFactory()
    private lateinit var factory: TestDataFactory
    private lateinit var owner: ApplicationUser
    private lateinit var reviewer: ApplicationUser
    private lateinit var systematicStudyId: UUID

    private val studyId = 1L
    private val otherReviewerA = UUID.randomUUID()
    private val otherReviewerB = UUID.randomUUID()

    private fun reviewsUrl(stage: String, ids: String = "$studyId", id: UUID = systematicStudyId) =
        "/api/v1/systematic-study/$id/agreement/study-review/reviews?stage=$stage&studyReviewIds=$ids"

    private fun reviewerListUrl(reviewerId: UUID, stage: String? = null) =
        "/api/v1/systematic-study/$systematicStudyId/study-review/reviewer/$reviewerId" +
                (stage?.let { "?stage=$it" } ?: "")

    private fun copyOf(reviewerId: UUID, stage: StudyReviewStage, status: String): ReviewerStudyDocument {
        val doc = factory.reviewerStudyDocument(systematicStudyId, studyId, reviewerId, stage)
        return doc.copy(studyReview = doc.studyReview.copy(selectionStatus = status, extractionStatus = status))
    }

    private fun givenVisibility(selection: AgreementVisibility, extraction: AgreementVisibility = selection) {
        visibilityRepository.save(AgreementVisibilityDocument(systematicStudyId, selection, extraction))
    }

    private fun givenOtherReviewersVotes(stage: StudyReviewStage = StudyReviewStage.SELECTION) {
        reviewerStudyRepository.insert(copyOf(otherReviewerA, stage, "INCLUDED"))
        reviewerStudyRepository.insert(copyOf(otherReviewerB, stage, "EXCLUDED"))
    }

    private fun get(user: ApplicationUser, url: String) =
        mockMvc.perform(
            get(url)
                .with(SecurityMockMvcRequestPostProcessors.user(user))
                .contentType(MediaType.APPLICATION_JSON)
        )

    @BeforeEach
    fun setUp() {
        reviewerStudyRepository.deleteAll()
        visibilityRepository.deleteAll()
        collaboratorRepository.deleteAll()

        factory = TestDataFactory()
        systematicStudyId = factory.systematicStudyId

        owner = testHelperService.createApplicationUser()
        reviewer = testHelperService.createApplicationUser()

        systematicStudyRepository.deleteAll()
        systematicStudyRepository.save(
            systematicStudyFactory.createSystematicStudyDocument(
                id = systematicStudyId,
                owner = owner.id,
                collaborators = mutableSetOf(reviewer.id),
            )
        )
        collaboratorRepository.save(systematicStudyFactory.createCollaboratorDocument(owner, systematicStudyId)) // OWNER
        collaboratorRepository.save(
            CollaboratorDocument(
                CollaboratorDocument.buildId(reviewer.id, systematicStudyId),
                reviewer.id,
                systematicStudyId,
                reviewer.username,
                "reviewer@test.com",
                "REVIEWER",
            )
        )
    }

    @AfterEach
    fun teardown() {
        reviewerStudyRepository.deleteAll()
        visibilityRepository.deleteAll()
        collaboratorRepository.deleteAll()
        systematicStudyRepository.deleteAll()
        testHelperService.deleteApplicationUser(owner.id)
        testHelperService.deleteApplicationUser(reviewer.id)
    }

    @Nested
    @DisplayName("When getting the reviewers' votes in the agreement")
    inner class AgreementReviewsTests {

        @Test
        fun `owner should see the votes even when the visibility is NONE and return 200`() {
            givenVisibility(AgreementVisibility.NONE)
            givenOtherReviewersVotes()

            get(owner, reviewsUrl("SELECTION"))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.canSeeReviews").value(true))
                .andExpect(jsonPath("$.visibility").value("NONE"))
                .andExpect(jsonPath("$.studies[0].studyReviewId").value(studyId))
                .andExpect(jsonPath("$.studies[0].reviews.length()").value(2))
        }

        @Test
        fun `reviewer should see the votes when the visibility is ALL and return 200`() {
            givenVisibility(AgreementVisibility.ALL)
            givenOtherReviewersVotes()

            get(reviewer, reviewsUrl("SELECTION"))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.canSeeReviews").value(true))
                .andExpect(jsonPath("$.studies[0].reviews.length()").value(2))
        }

        @Test
        fun `reviewer should not see the votes when the visibility is NONE and return 200 with the reason`() {
            givenVisibility(AgreementVisibility.NONE)
            givenOtherReviewersVotes()

            get(reviewer, reviewsUrl("SELECTION"))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.canSeeReviews").value(false))
                .andExpect(jsonPath("$.reason").value("HIDDEN"))
                .andExpect(jsonPath("$.studies[0].reviews.length()").value(0))
        }

        @Test
        fun `reviewer who has not finished the phase should not see the votes when FINISHED_ONLY`() {
            givenVisibility(AgreementVisibility.FINISHED_ONLY)
            givenOtherReviewersVotes()
            reviewerStudyRepository.insert(copyOf(reviewer.id, StudyReviewStage.SELECTION, "UNCLASSIFIED"))

            get(reviewer, reviewsUrl("SELECTION"))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.canSeeReviews").value(false))
                .andExpect(jsonPath("$.reason").value("NOT_FINISHED"))
                .andExpect(jsonPath("$.studies[0].reviews.length()").value(0))
        }

        @Test
        fun `reviewer who finished the phase should see the votes when FINISHED_ONLY`() {
            givenVisibility(AgreementVisibility.FINISHED_ONLY)
            givenOtherReviewersVotes()
            reviewerStudyRepository.insert(copyOf(reviewer.id, StudyReviewStage.SELECTION, "INCLUDED"))

            get(reviewer, reviewsUrl("SELECTION"))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.canSeeReviews").value(true))
                .andExpect(jsonPath("$.studies[0].reviews.length()").value(3))
        }

        @Test
        fun `should use FINISHED_ONLY by default and hide the votes from a user without studies in the stage`() {
            givenOtherReviewersVotes()

            get(reviewer, reviewsUrl("SELECTION"))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.visibility").value("FINISHED_ONLY"))
                .andExpect(jsonPath("$.canSeeReviews").value(false))
                .andExpect(jsonPath("$.reason").value("NOT_FINISHED"))
        }

        @Test
        fun `visibility should be independent for each stage`() {
            givenVisibility(selection = AgreementVisibility.NONE, extraction = AgreementVisibility.ALL)
            givenOtherReviewersVotes(StudyReviewStage.SELECTION)
            givenOtherReviewersVotes(StudyReviewStage.EXTRACTION)

            get(reviewer, reviewsUrl("SELECTION"))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.canSeeReviews").value(false))
            get(reviewer, reviewsUrl("EXTRACTION"))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.canSeeReviews").value(true))
                .andExpect(jsonPath("$.studies[0].reviews.length()").value(2))
        }

        @Test
        fun `should return 400 when the stage is missing`() {
            get(owner, "/api/v1/systematic-study/$systematicStudyId/agreement/study-review/reviews?studyReviewIds=1")
                .andExpect(status().isBadRequest)
        }

        @Test
        fun `should return 400 when the study ids are missing`() {
            get(owner, "/api/v1/systematic-study/$systematicStudyId/agreement/study-review/reviews?stage=SELECTION")
                .andExpect(status().isBadRequest)
        }

        @Test
        fun `should return 404 for a nonexistent systematic study`() {
            get(owner, reviewsUrl("SELECTION", id = UUID.randomUUID()))
                .andExpect(status().isNotFound)
        }

        @Test
        fun `should not get the votes if user is unauthenticated`() {
            testHelperService.testForUnauthenticatedUser(mockMvc, get(reviewsUrl("SELECTION")))
        }

        @Test
        fun `should not get the votes if user is unauthorized`() {
            testHelperService.testForUnauthorizedUser(mockMvc, get(reviewsUrl("SELECTION")))
        }
    }
}