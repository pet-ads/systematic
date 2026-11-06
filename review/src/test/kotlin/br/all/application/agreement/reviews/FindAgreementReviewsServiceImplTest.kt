package br.all.application.agreement.reviews

import br.all.application.agreement.reviews.FindAgreementReviewsService.RequestModel
import br.all.application.agreement.reviews.FindAgreementReviewsService.ResponseModel
import br.all.application.agreement.visibility.AgreementVisibilityPolicy
import br.all.application.reviewmode.repository.ReviewerStudyDto
import br.all.application.reviewmode.repository.ReviewerStudySearchRepository
import br.all.application.shared.service.AuthorizationService
import br.all.application.study.util.TestDataFactory
import br.all.domain.model.agreement.AgreementVisibility
import br.all.domain.model.study.StudyReviewStage
import br.all.domain.shared.user.Role
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.*

@Tag("UnitTest")
@Tag("ServiceTest")
@ExtendWith(MockKExtension::class)
class FindAgreementReviewsServiceImplTest {

    @MockK
    private lateinit var authorizationService: AuthorizationService

    @MockK
    private lateinit var authorizationContext: AuthorizationService.AuthorizationContext

    @MockK
    private lateinit var agreementVisibilityPolicy: AgreementVisibilityPolicy

    @MockK
    private lateinit var reviewerStudySearchRepository: ReviewerStudySearchRepository

    @MockK(relaxed = true)
    private lateinit var presenter: FindAgreementReviewsPresenter

    @InjectMockKs
    private lateinit var sut: FindAgreementReviewsServiceImpl

    private val factory = TestDataFactory()
    private val userId = UUID.randomUUID()
    private val systematicStudyId = UUID.randomUUID()

    private fun copy(studyId: Long, reviewerId: UUID, selection: String, extraction: String = "UNCLASSIFIED"): ReviewerStudyDto {
        val base = factory.generateReviewerStudyDto(studyId, reviewerId, StudyReviewStage.SELECTION)
        return base.copy(
            systematicStudyId = systematicStudyId,
            studyReview = base.studyReview.copy(selectionStatus = selection, extractionStatus = extraction)
        )
    }

    @BeforeEach
    fun setUp() {
        every {
            authorizationService.authorize(presenter, userId, systematicStudyId, Role.entries.toSet())
        } returns authorizationContext
        every { authorizationContext.role } returns Role.REVIEWER
    }

    @Test
    fun `should return the reviewers' votes of each study when the policy allows`() {
        val reviewerA = UUID.fromString("00000000-0000-0000-0000-00000000000a")
        val reviewerB = UUID.fromString("00000000-0000-0000-0000-00000000000b")
        every {
            agreementVisibilityPolicy.evaluate(userId, systematicStudyId, StudyReviewStage.SELECTION, Role.REVIEWER)
        } returns AgreementVisibilityPolicy.Decision(AgreementVisibility.ALL, true)
        every {
            reviewerStudySearchRepository.findAllByStudyIds(systematicStudyId, StudyReviewStage.SELECTION, listOf(1L, 2L))
        } returns listOf(
            copy(1L, reviewerB, "EXCLUDED"),
            copy(1L, reviewerA, "INCLUDED"),
            copy(2L, reviewerA, "UNCLASSIFIED"),
        )

        sut.findReviews(presenter, RequestModel(userId, systematicStudyId, StudyReviewStage.SELECTION, listOf(1L, 2L)))

        val response = slot<ResponseModel>()
        verify(exactly = 1) { presenter.prepareSuccessView(capture(response)) }
        assertTrue(response.captured.canSeeReviews)
        assertEquals(AgreementVisibility.ALL, response.captured.visibility)
        assertEquals(listOf(1L, 2L), response.captured.studies.map { it.studyReviewId })
        // ordenado por revisor
        assertEquals(listOf(reviewerA, reviewerB), response.captured.studies[0].reviews.map { it.reviewerId })
        assertEquals(listOf("INCLUDED", "EXCLUDED"), response.captured.studies[0].reviews.map { it.status })
        assertEquals(listOf("UNCLASSIFIED"), response.captured.studies[1].reviews.map { it.status })
    }

    @Test
    fun `should use the extraction status and criteria in the extraction stage`() {
        val reviewer = UUID.randomUUID()
        every {
            agreementVisibilityPolicy.evaluate(userId, systematicStudyId, StudyReviewStage.EXTRACTION, Role.REVIEWER)
        } returns AgreementVisibilityPolicy.Decision(AgreementVisibility.ALL, true)
        val extractionCopy = copy(1L, reviewer, selection = "INCLUDED", extraction = "EXCLUDED")
        every {
            reviewerStudySearchRepository.findAllByStudyIds(systematicStudyId, StudyReviewStage.EXTRACTION, listOf(1L))
        } returns listOf(extractionCopy)

        sut.findReviews(presenter, RequestModel(userId, systematicStudyId, StudyReviewStage.EXTRACTION, listOf(1L)))

        val response = slot<ResponseModel>()
        verify(exactly = 1) { presenter.prepareSuccessView(capture(response)) }
        val vote = response.captured.studies.single().reviews.single()
        assertEquals("EXCLUDED", vote.status)
        assertEquals(extractionCopy.studyReview.extractionCriteria, vote.criteria)
    }

    @Test
    fun `should hide the votes and explain why when the policy does not allow`() {
        every {
            agreementVisibilityPolicy.evaluate(userId, systematicStudyId, StudyReviewStage.SELECTION, Role.REVIEWER)
        } returns AgreementVisibilityPolicy.Decision(
            AgreementVisibility.FINISHED_ONLY, false, AgreementVisibilityPolicy.Reason.NOT_FINISHED
        )

        sut.findReviews(presenter, RequestModel(userId, systematicStudyId, StudyReviewStage.SELECTION, listOf(1L, 2L)))

        val response = slot<ResponseModel>()
        verify(exactly = 1) { presenter.prepareSuccessView(capture(response)) }
        assertEquals(false, response.captured.canSeeReviews)
        assertEquals(AgreementVisibilityPolicy.Reason.NOT_FINISHED, response.captured.reason)
        assertEquals(listOf(1L, 2L), response.captured.studies.map { it.studyReviewId })
        assertTrue(response.captured.studies.all { it.reviews.isEmpty() })
        verify(exactly = 0) { reviewerStudySearchRepository.findAllByStudyIds(any(), any(), any()) }
    }

    @Test
    fun `should not do anything when the user is not authorized`() {
        every {
            authorizationService.authorize(presenter, userId, systematicStudyId, Role.entries.toSet())
        } returns null

        sut.findReviews(presenter, RequestModel(userId, systematicStudyId, StudyReviewStage.SELECTION, listOf(1L)))

        verify(exactly = 0) { agreementVisibilityPolicy.evaluate(any(), any(), any(), any()) }
        verify(exactly = 0) { presenter.prepareSuccessView(any()) }
    }

    @Test
    fun `should fail when no study is informed`() {
        sut.findReviews(presenter, RequestModel(userId, systematicStudyId, StudyReviewStage.SELECTION, emptyList()))

        verify(exactly = 1) { presenter.prepareFailView(any<IllegalArgumentException>()) }
        verify(exactly = 0) { agreementVisibilityPolicy.evaluate(any(), any(), any(), any()) }
    }

    @Test
    fun `should fail when too many studies are informed`() {
        val ids = (1L..101L).toList()

        sut.findReviews(presenter, RequestModel(userId, systematicStudyId, StudyReviewStage.SELECTION, ids))

        verify(exactly = 1) { presenter.prepareFailView(any<IllegalArgumentException>()) }
    }
}