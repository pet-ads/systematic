package br.all.application.study.find.service

import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.reviewmode.repository.ReviewerStudyRepository
import br.all.application.study.find.presenter.FindStudyReviewPresenter
import br.all.application.study.util.TestDataFactory
import br.all.application.user.CredentialsService
import br.all.application.util.PreconditionCheckerMockingNew
import br.all.domain.model.study.StudyReviewStage
import br.all.domain.shared.exception.EntityNotFoundException
import br.all.domain.shared.exception.UnauthorizedUserException
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Tags
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.*

@Tags(Tag("UnitTest"), Tag("ServiceTest"))
@ExtendWith(MockKExtension::class)
class FindReviewerStudyServiceImplTest {

    @MockK private lateinit var systematicStudyRepository: SystematicStudyRepository
    @MockK private lateinit var reviewerStudyRepository: ReviewerStudyRepository
    @MockK private lateinit var credentialsService: CredentialsService
    @MockK(relaxed = true) private lateinit var presenter: FindStudyReviewPresenter

    @InjectMockKs
    private lateinit var sut: FindReviewerStudyServiceImpl

    private lateinit var factory: TestDataFactory
    private lateinit var preconditionCheckerMocking: PreconditionCheckerMockingNew

    @BeforeEach
    fun setUp() {
        factory = TestDataFactory()
        preconditionCheckerMocking = PreconditionCheckerMockingNew(
            presenter, credentialsService, systematicStudyRepository, factory.researcherId, factory.systematicStudyId
        )
    }

    @Nested
    @DisplayName("When finding a reviewer's study copy")
    inner class SuccessTests {

        @Test
        fun `should successfully find the reviewer's own study copy`() {
            val studyId = 1L
            val reviewerId = factory.researcherId
            val copy = factory.generateReviewerStudyDto(studyId, reviewerId, StudyReviewStage.SELECTION)
            val request = FindReviewerStudyService.RequestModel(
                userId = reviewerId,
                systematicStudyId = factory.systematicStudyId,
                reviewerId = reviewerId,
                studyReviewId = studyId,
                stage = StudyReviewStage.SELECTION
            )

            preconditionCheckerMocking.makeEverythingWork()
            every {
                reviewerStudyRepository.findById(factory.systematicStudyId, reviewerId, studyId, StudyReviewStage.SELECTION)
            } returns copy

            sut.findOne(presenter, request)

            verify(exactly = 1) {
                presenter.prepareSuccessView(FindStudyReviewService.ResponseModel(reviewerId, copy.studyReview))
            }
        }
    }

    @Nested
    @DisplayName("When unable to find a reviewer's study copy")
    inner class FailureTests {

        @Test
        fun `should fail when reviewer is trying to access another reviewer's copy`() {
            val userId = factory.researcherId
            val otherReviewerId = UUID.randomUUID()
            val request = FindReviewerStudyService.RequestModel(
                userId = userId,
                systematicStudyId = factory.systematicStudyId,
                reviewerId = otherReviewerId,
                studyReviewId = 1L,
                stage = StudyReviewStage.SELECTION
            )

            preconditionCheckerMocking.makeEverythingWork()

            sut.findOne(presenter, request)

            verify(exactly = 1) { presenter.prepareFailView(any<UnauthorizedUserException>()) }
            verify(exactly = 0) { reviewerStudyRepository.findById(any(), any(), any(), any()) }
        }

        @Test
        fun `should fail when the study copy is not assigned to the reviewer`() {
            val reviewerId = factory.researcherId
            val request = FindReviewerStudyService.RequestModel(
                userId = reviewerId,
                systematicStudyId = factory.systematicStudyId,
                reviewerId = reviewerId,
                studyReviewId = 999L,
                stage = StudyReviewStage.SELECTION
            )

            preconditionCheckerMocking.makeEverythingWork()
            every { reviewerStudyRepository.findById(any(), any(), any(), any()) } returns null

            sut.findOne(presenter, request)

            verify(exactly = 1) { presenter.prepareFailView(any<EntityNotFoundException>()) }
        }

        @Test
        fun `should fail when user is unauthenticated`() {
            val reviewerId = factory.researcherId
            val request = FindReviewerStudyService.RequestModel(
                userId = reviewerId,
                systematicStudyId = factory.systematicStudyId,
                reviewerId = reviewerId,
                studyReviewId = 1L,
                stage = StudyReviewStage.SELECTION
            )

            preconditionCheckerMocking.makeUserUnauthenticated()
            sut.findOne(presenter, request)

            verify(exactly = 1) { presenter.prepareFailView(any()) }
        }

        @Test
        fun `should fail when user is unauthorized`() {
            val reviewerId = factory.researcherId
            val request = FindReviewerStudyService.RequestModel(
                userId = reviewerId,
                systematicStudyId = factory.systematicStudyId,
                reviewerId = reviewerId,
                studyReviewId = 1L,
                stage = StudyReviewStage.SELECTION
            )

            preconditionCheckerMocking.makeUserUnauthorized()
            sut.findOne(presenter, request)

            verify(exactly = 1) { presenter.prepareFailView(any()) }
        }

        @Test
        fun `should fail when systematic study does not exist`() {
            val reviewerId = factory.researcherId
            val request = FindReviewerStudyService.RequestModel(
                userId = reviewerId,
                systematicStudyId = factory.systematicStudyId,
                reviewerId = reviewerId,
                studyReviewId = 1L,
                stage = StudyReviewStage.SELECTION
            )

            preconditionCheckerMocking.makeSystematicStudyNonexistent()
            sut.findOne(presenter, request)

            verify(exactly = 1) { presenter.prepareFailView(any()) }
        }
    }
}