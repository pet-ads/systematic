package br.all.application.agreement.visibility

import br.all.application.agreement.repository.AgreementVisibilityDto
import br.all.application.agreement.repository.AgreementVisibilityRepository
import br.all.application.reviewmode.repository.ReviewerStudySearchRepository
import br.all.domain.model.agreement.AgreementVisibility
import br.all.domain.model.study.StudyReviewStage
import br.all.domain.shared.user.Role
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.*

@Tag("UnitTest")
@Tag("ServiceTest")
@ExtendWith(MockKExtension::class)
class AgreementVisibilityPolicyTest {

    @MockK
    private lateinit var agreementVisibilityRepository: AgreementVisibilityRepository

    @MockK
    private lateinit var reviewerStudySearchRepository: ReviewerStudySearchRepository

    @InjectMockKs
    private lateinit var sut: AgreementVisibilityPolicy

    private val userId = UUID.randomUUID()
    private val systematicStudyId = UUID.randomUUID()

    private fun givenVisibility(selection: AgreementVisibility, extraction: AgreementVisibility = selection) {
        every { agreementVisibilityRepository.findById(systematicStudyId) } returns
                AgreementVisibilityDto(systematicStudyId, selection, extraction)
    }

    private fun givenCounts(stage: StudyReviewStage, assigned: Long, unclassified: Long, attribute: String) {
        every { reviewerStudySearchRepository.countAssigned(systematicStudyId, userId, stage) } returns assigned
        every {
            reviewerStudySearchRepository.countByStatus(systematicStudyId, userId, stage, attribute, "UNCLASSIFIED")
        } returns unclassified
    }

    @Test
    fun `owner should see the reviews even when the visibility is NONE`() {
        givenVisibility(AgreementVisibility.NONE)

        val decision = sut.evaluate(userId, systematicStudyId, StudyReviewStage.SELECTION, Role.OWNER)

        assertTrue(decision.allowed)
        assertEquals(AgreementVisibility.NONE, decision.visibility)
        verify(exactly = 0) { reviewerStudySearchRepository.countAssigned(any(), any(), any()) }
    }

    @Test
    fun `admin should see the reviews even when the visibility is FINISHED_ONLY and the phase is not finished`() {
        givenVisibility(AgreementVisibility.FINISHED_ONLY)

        val decision = sut.evaluate(userId, systematicStudyId, StudyReviewStage.SELECTION, Role.ADMIN)

        assertTrue(decision.allowed)
        verify(exactly = 0) { reviewerStudySearchRepository.countAssigned(any(), any(), any()) }
    }

    @Test
    fun `reviewer should see the reviews when the visibility is ALL`() {
        givenVisibility(AgreementVisibility.ALL)

        val decision = sut.evaluate(userId, systematicStudyId, StudyReviewStage.SELECTION, Role.REVIEWER)

        assertTrue(decision.allowed)
        assertNull(decision.reason)
    }

    @Test
    fun `reviewer should not see the reviews when the visibility is NONE`() {
        givenVisibility(AgreementVisibility.NONE)

        val decision = sut.evaluate(userId, systematicStudyId, StudyReviewStage.SELECTION, Role.REVIEWER)

        assertFalse(decision.allowed)
        assertEquals(AgreementVisibilityPolicy.Reason.HIDDEN, decision.reason)
    }

    @Test
    fun `reviewer who finished the selection should see the reviews when the visibility is FINISHED_ONLY`() {
        givenVisibility(AgreementVisibility.FINISHED_ONLY)
        givenCounts(StudyReviewStage.SELECTION, assigned = 3, unclassified = 0, attribute = "selectionStatus")

        val decision = sut.evaluate(userId, systematicStudyId, StudyReviewStage.SELECTION, Role.REVIEWER)

        assertTrue(decision.allowed)
        assertNull(decision.reason)
    }

    @Test
    fun `reviewer with unclassified studies should not see the reviews when the visibility is FINISHED_ONLY`() {
        givenVisibility(AgreementVisibility.FINISHED_ONLY)
        givenCounts(StudyReviewStage.SELECTION, assigned = 3, unclassified = 1, attribute = "selectionStatus")

        val decision = sut.evaluate(userId, systematicStudyId, StudyReviewStage.SELECTION, Role.REVIEWER)

        assertFalse(decision.allowed)
        assertEquals(AgreementVisibilityPolicy.Reason.NOT_FINISHED, decision.reason)
    }

    @Test
    fun `user without studies assigned in the stage has not finished it`() {
        givenVisibility(AgreementVisibility.FINISHED_ONLY)
        every { reviewerStudySearchRepository.countAssigned(systematicStudyId, userId, StudyReviewStage.SELECTION) } returns 0

        val decision = sut.evaluate(userId, systematicStudyId, StudyReviewStage.SELECTION, Role.VIEWER)

        assertFalse(decision.allowed)
        assertEquals(AgreementVisibilityPolicy.Reason.NOT_FINISHED, decision.reason)
        verify(exactly = 0) { reviewerStudySearchRepository.countByStatus(any(), any(), any(), any(), any()) }
    }

    @Test
    fun `extraction should check the extraction status`() {
        givenVisibility(AgreementVisibility.FINISHED_ONLY)
        givenCounts(StudyReviewStage.EXTRACTION, assigned = 2, unclassified = 0, attribute = "extractionStatus")

        val decision = sut.evaluate(userId, systematicStudyId, StudyReviewStage.EXTRACTION, Role.REVIEWER)

        assertTrue(decision.allowed)
        verify(exactly = 1) {
            reviewerStudySearchRepository.countByStatus(
                systematicStudyId, userId, StudyReviewStage.EXTRACTION, "extractionStatus", "UNCLASSIFIED"
            )
        }
    }

    @Test
    fun `visibility should be evaluated per stage`() {
        givenVisibility(selection = AgreementVisibility.NONE, extraction = AgreementVisibility.ALL)

        assertFalse(sut.evaluate(userId, systematicStudyId, StudyReviewStage.SELECTION, Role.REVIEWER).allowed)
        assertTrue(sut.evaluate(userId, systematicStudyId, StudyReviewStage.EXTRACTION, Role.REVIEWER).allowed)
    }

    @Test
    fun `should use the default visibility when none was configured`() {
        every { agreementVisibilityRepository.findById(systematicStudyId) } returns null
        givenCounts(StudyReviewStage.SELECTION, assigned = 1, unclassified = 1, attribute = "selectionStatus")

        val decision = sut.evaluate(userId, systematicStudyId, StudyReviewStage.SELECTION, Role.REVIEWER)

        assertEquals(AgreementVisibility.DEFAULT, decision.visibility)
        assertFalse(decision.allowed)
    }
}