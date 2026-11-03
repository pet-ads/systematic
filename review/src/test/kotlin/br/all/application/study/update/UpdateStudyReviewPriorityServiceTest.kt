package br.all.application.study.update

import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.reviewmode.repository.ReviewerStudyDto
import br.all.application.reviewmode.repository.ReviewerStudyRepository
import br.all.domain.shared.exception.EntityNotFoundException
import br.all.application.study.repository.StudyReviewRepository
import br.all.application.study.update.implementation.UpdateStudyReviewPriorityService
import br.all.application.study.update.interfaces.UpdateStudyReviewStatusPresenter
import br.all.application.study.util.TestDataFactory
import br.all.application.user.CredentialsService
import br.all.application.util.PreconditionCheckerMockingNew
import br.all.domain.model.study.StudyReviewStage
import br.all.domain.shared.exception.UnauthorizedUserException
import io.mockk.*
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.extension.ExtendWith
import java.util.UUID

@Tag("UnitTest")
@Tag("ServiceTest")
@ExtendWith(MockKExtension::class)
class UpdateStudyReviewPriorityServiceTest {

    @MockK(relaxed = true) private lateinit var studyReviewRepository: StudyReviewRepository
    @MockK(relaxUnitFun = true) private lateinit var systematicStudyRepository: SystematicStudyRepository
    @MockK private lateinit var credentialService: CredentialsService
    @MockK(relaxed = true) private lateinit var presenter: UpdateStudyReviewStatusPresenter
    @MockK private lateinit var reviewerStudyRepository: ReviewerStudyRepository

    private lateinit var sut: UpdateStudyReviewPriorityService

    private lateinit var factory: TestDataFactory
    private lateinit var preconditionCheckerMocking: PreconditionCheckerMockingNew

    private fun reviewerSut() = UpdateStudyReviewPriorityService(
        systematicStudyRepository, studyReviewRepository, credentialService, reviewerStudyRepository
    )

    @BeforeEach
    fun setUp() {
        factory = TestDataFactory()
        preconditionCheckerMocking = PreconditionCheckerMockingNew(
            presenter,
            credentialService,
            systematicStudyRepository,
            factory.researcherId,
            factory.systematicStudyId
        )
        sut = UpdateStudyReviewPriorityService(
            systematicStudyRepository,
            studyReviewRepository,
            credentialService,
        )
    }

    @Nested
    @Tag("ValidClasses")
    @DisplayName("When successfully updating a Study Review's reading priority")
    inner class WhenSuccessfullyUpdatingAStudyReviewPriority {
        @Test
        fun `should successfully update a Study Review's reading priority`() {
            val dto = factory.generateDto()
            val request = factory.updateStatusRequestModel("HIGH", setOf("criteria1", "criteria2"))

            preconditionCheckerMocking.makeEverythingWork()

            every { studyReviewRepository.findById(request.systematicStudyId, request.studyReviewId.first()) } returns dto

            sut.changeStatus(presenter, request)

            verify(exactly = 1) {
                studyReviewRepository.saveOrUpdate(any())
                presenter.prepareSuccessView(any())
            }
        }

        @Test
        fun `should update the reading priority of the reviewer's own copy`() {
            val request = factory.updateStatusRequestModel("HIGH", emptySet())
                .copy(reviewerId = factory.researcherId, stage = StudyReviewStage.SELECTION)
            val studyId = request.studyReviewId.first()
            val copy = factory.generateReviewerStudyDto(studyId, factory.researcherId)

            preconditionCheckerMocking.makeEverythingWork()
            every {
                reviewerStudyRepository.findById(request.systematicStudyId, factory.researcherId, studyId, StudyReviewStage.SELECTION)
            } returns copy
            every { reviewerStudyRepository.saveOrUpdate(any()) } just Runs

            reviewerSut().changeStatus(presenter, request)

            val saved = slot<ReviewerStudyDto>()
            verify(exactly = 1) { reviewerStudyRepository.saveOrUpdate(capture(saved)) }
            assertEquals("HIGH", saved.captured.studyReview.readingPriority)
            verify(exactly = 0) { studyReviewRepository.saveOrUpdate(any()) }
            verify(exactly = 1) { presenter.prepareSuccessView(any()) }
        }
    }

    @Nested
    @Tag("InvalidClasses")
    @DisplayName("When failing to update a study review's reading priority")
    inner class WhenFailingToUpdateAStudyReviewPriority {
        @Test
        fun `should not be able to update a non-existent study`() {
            val request = factory.updateStatusRequestModel("HIGH", setOf("criteria1", "criteria2"))

            preconditionCheckerMocking.makeEverythingWork()

            every { studyReviewRepository.findById(request.systematicStudyId, request.studyReviewId.first()) } returns null
            sut.changeStatus(presenter, request)

            verify {
                presenter.prepareFailView(any<EntityNotFoundException>())
            }
        }

        @Test
        fun `should not update when unauthenticated`() {
            val request = factory.updateStatusRequestModel("status!", setOf("criteria1", "criteria2"))

            preconditionCheckerMocking.testForUnauthenticatedUser(presenter, request) { _, _ ->
                sut.changeStatus(presenter, request)
            }

        }

        @Test
        fun `should not update when unauthorized`() {
            val request = factory.updateStatusRequestModel("status!", setOf("criteria1", "criteria2"))

            preconditionCheckerMocking.testForUnauthorizedUser(presenter, request) { _, _ ->
                sut.changeStatus(presenter, request)
            }

        }

        @Test
        fun `should not update when systematic study doesnt exist`() {
            val request = factory.updateStatusRequestModel("status!", setOf("criteria1", "criteria2"))

            preconditionCheckerMocking.testForNonexistentSystematicStudy(presenter, request) { _, _ ->
                sut.changeStatus(presenter, request)
            }

        }

        @Test
        fun `should require the stage when the reviewer is informed`() {
            val request = factory.updateStatusRequestModel("HIGH", emptySet())
                .copy(reviewerId = factory.researcherId)

            preconditionCheckerMocking.makeEverythingWork()

            reviewerSut().changeStatus(presenter, request)

            verify(exactly = 1) { presenter.prepareFailView(any<IllegalArgumentException>()) }
        }

        @Test
        fun `should not change the priority of another reviewer's copy`() {
            val request = factory.updateStatusRequestModel("HIGH", emptySet())
                .copy(reviewerId = UUID.randomUUID(), stage = StudyReviewStage.SELECTION)

            preconditionCheckerMocking.makeEverythingWork()

            reviewerSut().changeStatus(presenter, request)

            verify(exactly = 1) { presenter.prepareFailView(any<UnauthorizedUserException>()) }
            verify(exactly = 0) { reviewerStudyRepository.saveOrUpdate(any()) }
        }

        @Test
        fun `should not find a copy that was not assigned to the reviewer`() {
            val request = factory.updateStatusRequestModel("HIGH", emptySet())
                .copy(reviewerId = factory.researcherId, stage = StudyReviewStage.SELECTION)

            preconditionCheckerMocking.makeEverythingWork()
            every { reviewerStudyRepository.findById(any(), any(), any(), any()) } returns null

            reviewerSut().changeStatus(presenter, request)

            verify(exactly = 1) { presenter.prepareFailView(any<EntityNotFoundException>()) }
        }
    }

}