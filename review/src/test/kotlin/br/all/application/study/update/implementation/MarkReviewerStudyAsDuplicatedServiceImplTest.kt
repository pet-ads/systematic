package br.all.application.study.update

import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.reviewmode.repository.ReviewerStudyDto
import br.all.application.reviewmode.repository.ReviewerStudyRepository
import br.all.application.study.repository.StudyReviewRepository
import br.all.application.study.update.implementation.MarkReviewerStudyAsDuplicatedServiceImpl
import br.all.application.study.update.interfaces.MarkAsDuplicatedPresenter
import br.all.application.study.update.interfaces.MarkReviewerStudyAsDuplicatedService.RequestModel
import br.all.application.study.util.TestDataFactory
import br.all.application.user.CredentialsService
import br.all.application.util.PreconditionCheckerMockingNew
import br.all.domain.model.study.StudyReviewStage
import br.all.domain.shared.exception.EntityNotFoundException
import br.all.domain.shared.exception.UnauthorizedUserException
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.*

@Tag("UnitTest")
@Tag("ServiceTest")
@ExtendWith(MockKExtension::class)
class MarkReviewerStudyAsDuplicatedServiceImplTest {

    @MockK(relaxed = true) private lateinit var studyReviewRepository: StudyReviewRepository
    @MockK private lateinit var reviewerStudyRepository: ReviewerStudyRepository
    @MockK(relaxUnitFun = true) private lateinit var systematicStudyRepository: SystematicStudyRepository
    @MockK private lateinit var credentialService: CredentialsService
    @MockK(relaxed = true) private lateinit var presenter: MarkAsDuplicatedPresenter

    private lateinit var sut: MarkReviewerStudyAsDuplicatedServiceImpl

    private lateinit var factory: TestDataFactory
    private lateinit var preconditionCheckerMocking: PreconditionCheckerMockingNew
    private lateinit var reviewerId: UUID

    private val referenceId = 10L
    private val duplicateId = 1L

    @BeforeEach
    fun setUp() {
        factory = TestDataFactory()
        reviewerId = factory.researcherId
        preconditionCheckerMocking = PreconditionCheckerMockingNew(
            presenter,
            credentialService,
            systematicStudyRepository,
            factory.researcherId,
            factory.systematicStudyId
        )
        sut = MarkReviewerStudyAsDuplicatedServiceImpl(
            systematicStudyRepository,
            studyReviewRepository,
            reviewerStudyRepository,
            credentialService,
        )
    }

    private fun request(
        stage: StudyReviewStage = StudyReviewStage.SELECTION,
        reviewer: UUID = reviewerId,
        reference: Long = referenceId,
        duplicates: List<Long> = listOf(duplicateId),
    ) = RequestModel(factory.researcherId, factory.systematicStudyId, reviewer, reference, duplicates, stage)

    private fun copy(studyId: Long, stage: StudyReviewStage): ReviewerStudyDto {
        val base = factory.generateReviewerStudyDto(studyId, reviewerId, stage)
        return base.copy(
            studyReview = base.studyReview.copy(selectionStatus = "UNCLASSIFIED", extractionStatus = "UNCLASSIFIED")
        )
    }

    private fun givenCopy(studyId: Long, stage: StudyReviewStage): ReviewerStudyDto {
        val copy = copy(studyId, stage)
        every {
            reviewerStudyRepository.findById(factory.systematicStudyId, reviewerId, studyId, stage)
        } returns copy
        return copy
    }

    private fun capturingSaves(): MutableList<ReviewerStudyDto> {
        val saved = mutableListOf<ReviewerStudyDto>()
        every { reviewerStudyRepository.saveOrUpdate(capture(saved)) } just Runs
        return saved
    }

    @Nested
    @Tag("ValidClasses")
    @DisplayName("When successfully marking the reviewer's copies as duplicated")
    inner class WhenSuccessfullyMarkingAsDuplicated {

        @BeforeEach
        fun setUp() = run { preconditionCheckerMocking.makeEverythingWork() }

        @Test
        fun `should mark the duplicate copy as duplicated in selection`() {
            givenCopy(referenceId, StudyReviewStage.SELECTION)
            givenCopy(duplicateId, StudyReviewStage.SELECTION)
            val saved = capturingSaves()

            sut.markAsDuplicated(presenter, request())

            assertEquals(2, saved.size)
            val duplicate = saved.single { it.studyId == duplicateId }
            assertEquals("DUPLICATED", duplicate.studyReview.selectionStatus)
            assertEquals("DUPLICATED", duplicate.studyReview.extractionStatus)
            val reference = saved.single { it.studyId == referenceId }
            assertNotEquals("DUPLICATED", reference.studyReview.selectionStatus)
            verify(exactly = 1) { presenter.prepareSuccessView(any()) }
        }

        @Test
        fun `should mark only the extraction status as duplicated in extraction`() {
            givenCopy(referenceId, StudyReviewStage.EXTRACTION)
            givenCopy(duplicateId, StudyReviewStage.EXTRACTION)
            val saved = capturingSaves()

            sut.markAsDuplicated(presenter, request(stage = StudyReviewStage.EXTRACTION))

            val duplicate = saved.single { it.studyId == duplicateId }
            assertEquals("DUPLICATED", duplicate.studyReview.extractionStatus)
            assertNotEquals("DUPLICATED", duplicate.studyReview.selectionStatus)
            verify(exactly = 1) { presenter.prepareSuccessView(any()) }
        }

        @Test
        fun `should keep the copy in the stage it was assigned`() {
            givenCopy(referenceId, StudyReviewStage.EXTRACTION)
            givenCopy(duplicateId, StudyReviewStage.EXTRACTION)
            val saved = capturingSaves()

            sut.markAsDuplicated(presenter, request(stage = StudyReviewStage.EXTRACTION))

            assertEquals(setOf(StudyReviewStage.EXTRACTION), saved.map { it.stage }.toSet())
            assertEquals(setOf(reviewerId), saved.map { it.researcherId }.toSet())
        }

        @Test
        fun `should never touch the original study reviews`() {
            givenCopy(referenceId, StudyReviewStage.SELECTION)
            givenCopy(duplicateId, StudyReviewStage.SELECTION)
            capturingSaves()

            sut.markAsDuplicated(presenter, request())

            verify(exactly = 0) { studyReviewRepository.saveOrUpdate(any()) }
        }

        @Test
        fun `should mark several copies at once`() {
            givenCopy(referenceId, StudyReviewStage.SELECTION)
            givenCopy(1L, StudyReviewStage.SELECTION)
            givenCopy(2L, StudyReviewStage.SELECTION)
            val saved = capturingSaves()

            sut.markAsDuplicated(presenter, request(duplicates = listOf(1L, 2L)))

            assertEquals(3, saved.size)
            assertEquals(
                setOf(1L, 2L),
                saved.filter { it.studyReview.selectionStatus == "DUPLICATED" }.map { it.studyId }.toSet()
            )
        }
    }

    @Nested
    @Tag("InvalidClasses")
    @DisplayName("When failing to mark the reviewer's copies as duplicated")
    inner class WhenFailingToMarkAsDuplicated {

        @Test
        fun `should not let a reviewer mark another reviewer's copies`() {
            preconditionCheckerMocking.makeEverythingWork()

            sut.markAsDuplicated(presenter, request(reviewer = UUID.randomUUID()))

            verify(exactly = 1) { presenter.prepareFailView(any<UnauthorizedUserException>()) }
            verify(exactly = 0) { reviewerStudyRepository.saveOrUpdate(any()) }
        }

        @Test
        fun `should not mark a study as duplicate of itself`() {
            preconditionCheckerMocking.makeEverythingWork()

            sut.markAsDuplicated(presenter, request(duplicates = listOf(referenceId)))

            verify(exactly = 1) { presenter.prepareFailView(any<IllegalArgumentException>()) }
            verify(exactly = 0) { reviewerStudyRepository.saveOrUpdate(any()) }
        }

        @Test
        fun `should not find a reference copy that was not assigned to the reviewer`() {
            preconditionCheckerMocking.makeEverythingWork()
            every {
                reviewerStudyRepository.findById(factory.systematicStudyId, reviewerId, referenceId, StudyReviewStage.SELECTION)
            } returns null

            sut.markAsDuplicated(presenter, request())

            verify(exactly = 1) { presenter.prepareFailView(any<EntityNotFoundException>()) }
            verify(exactly = 0) { reviewerStudyRepository.saveOrUpdate(any()) }
        }

        @Test
        fun `should not find a duplicate copy that was not assigned to the reviewer`() {
            preconditionCheckerMocking.makeEverythingWork()
            givenCopy(referenceId, StudyReviewStage.SELECTION)
            every {
                reviewerStudyRepository.findById(factory.systematicStudyId, reviewerId, duplicateId, StudyReviewStage.SELECTION)
            } returns null

            sut.markAsDuplicated(presenter, request())

            verify(exactly = 1) { presenter.prepareFailView(any<EntityNotFoundException>()) }
            verify(exactly = 0) { reviewerStudyRepository.saveOrUpdate(any()) }
        }

        @Test
        fun `should not mark as duplicated when unauthenticated`() {
            val request = request()

            preconditionCheckerMocking.testForUnauthenticatedUser(presenter, request) { _, _ ->
                sut.markAsDuplicated(presenter, request)
            }
        }

        @Test
        fun `should not mark as duplicated when unauthorized`() {
            val request = request()

            preconditionCheckerMocking.testForUnauthorizedUser(presenter, request) { _, _ ->
                sut.markAsDuplicated(presenter, request)
            }
        }

        @Test
        fun `should not mark as duplicated when systematic study does not exist`() {
            val request = request()

            preconditionCheckerMocking.testForNonexistentSystematicStudy(presenter, request) { _, _ ->
                sut.markAsDuplicated(presenter, request)
            }
        }
    }
}