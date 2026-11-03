package br.all.application.study.access

import br.all.application.reviewmode.repository.ReviewerStudyRepository
import br.all.application.study.repository.StudyReviewRepository
import br.all.application.study.util.TestDataFactory
import br.all.domain.model.study.StudyReviewStage
import br.all.domain.shared.exception.UnauthorizedUserException
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.UUID

@Tag("UnitTest")
@Tag("ServiceTest")
@ExtendWith(MockKExtension::class)
class StudyReviewAccessTest {

    @MockK
    private lateinit var studyReviewRepository: StudyReviewRepository

    @MockK
    private lateinit var reviewerStudyRepository: ReviewerStudyRepository

    private lateinit var sut: StudyReviewAccess
    private lateinit var factory: TestDataFactory
    private lateinit var systematicStudyId: UUID
    private val studyId = 1L
    private val reviewerId = UUID.randomUUID()

    @BeforeEach
    fun setUp() {
        factory = TestDataFactory()
        systematicStudyId = factory.systematicStudyId
        sut = StudyReviewAccess(studyReviewRepository, reviewerStudyRepository)
    }

    @Test
    fun `should open the original study when no reviewer is informed and save it in the original collection`() {
        val dto = factory.generateDto(studyReviewId = studyId)
        every { studyReviewRepository.findById(systematicStudyId, studyId) } returns dto
        every { studyReviewRepository.saveOrUpdate(any()) } returns Unit

        val target = sut.open(systematicStudyId, studyId, null, null)

        assertNotNull(target)
        assertEquals(dto, target!!.dto)

        val updated = dto.copy(comments = "updated")
        target.save(updated)

        verify(exactly = 1) { studyReviewRepository.saveOrUpdate(updated) }
        verify(exactly = 0) { reviewerStudyRepository.saveOrUpdate(any()) }
    }

    @Test
    fun `should return null when the original study does not exist`() {
        every { studyReviewRepository.findById(systematicStudyId, studyId) } returns null

        assertNull(sut.open(systematicStudyId, studyId, null, null))
    }

    @Test
    fun `should open the reviewer's copy and save it wrapped in the reviewer study`() {
        val copy = factory.generateReviewerStudyDto(studyId, reviewerId, StudyReviewStage.EXTRACTION)

        every {
            reviewerStudyRepository.findById(
                systematicStudyId,
                reviewerId,
                studyId,
                StudyReviewStage.EXTRACTION
            )
        } returns copy

        every { reviewerStudyRepository.saveOrUpdate(any()) } returns Unit

        val target = sut.open(systematicStudyId, studyId, reviewerId, StudyReviewStage.EXTRACTION)

        assertNotNull(target)
        assertEquals(copy.studyReview, target!!.dto)

        val updated = copy.studyReview.copy(comments = "updated")
        target.save(updated)

        verify(exactly = 1) {
            reviewerStudyRepository.saveOrUpdate(copy.copy(studyReview = updated))
        }
        verify(exactly = 0) { studyReviewRepository.saveOrUpdate(any()) }
    }

    @Test
    fun `should return null when the study was not assigned to the reviewer`() {
        every {
            reviewerStudyRepository.findById(any(), any(), any(), any())
        } returns null

        assertNull(sut.open(systematicStudyId, studyId, reviewerId, StudyReviewStage.SELECTION))

        verify(exactly = 0) { studyReviewRepository.findById(any(), any()) }
    }

    @Test
    fun `should require the stage to open a reviewer's copy`() {
        assertThrows(IllegalArgumentException::class.java) {
            sut.open(systematicStudyId, studyId, reviewerId, null)
        }
    }

    @Test
    fun `should fail when a reviewer is informed but there is no reviewer study repository`() {
        val withoutRepository = StudyReviewAccess(studyReviewRepository, null)

        assertThrows(IllegalStateException::class.java) {
            withoutRepository.open(systematicStudyId, studyId, reviewerId, StudyReviewStage.SELECTION)
        }
    }

    @Test
    fun `should validate the context`() {
        val userId = UUID.randomUUID()

        assertNull(sut.validate(userId, null, null))
        assertNull(sut.validate(userId, userId, StudyReviewStage.SELECTION))
        assertTrue(sut.validate(userId, UUID.randomUUID(), StudyReviewStage.SELECTION) is UnauthorizedUserException)
        assertTrue(sut.validate(userId, userId, null) is IllegalArgumentException)
    }

    @Test
    fun `should only accept the own copy`() {
        val userId = UUID.randomUUID()

        assertTrue(sut.isOwnCopy(userId, null))
        assertTrue(sut.isOwnCopy(userId, userId))
        assertFalse(sut.isOwnCopy(userId, UUID.randomUUID()))
    }
}