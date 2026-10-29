package br.all.application.study.update

import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.reviewmode.repository.ReviewerStudyRepository
import br.all.application.study.majority.MajorityDecisionApplier
import br.all.domain.shared.exception.EntityNotFoundException
import br.all.application.study.repository.StudyReviewRepository
import br.all.application.study.update.implementation.UpdateStudyReviewExtractionService
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
import org.junit.jupiter.api.extension.ExtendWith
import java.util.UUID
import kotlin.test.assertFailsWith

@Tag("UnitTest")
@Tag("ServiceTest")
@ExtendWith(MockKExtension::class)
class UpdateStudyReviewExtractionServiceTest {

    @MockK(relaxed = true) private lateinit var studyReviewRepository: StudyReviewRepository
    @MockK(relaxUnitFun = true) private lateinit var systematicStudyRepository: SystematicStudyRepository
    @MockK private lateinit var credentialService: CredentialsService
    @MockK(relaxed = true) private lateinit var presenter: UpdateStudyReviewStatusPresenter
    @MockK private lateinit var reviewerStudyRepository: ReviewerStudyRepository
    @MockK(relaxed = true) private lateinit var majorityApplier: MajorityDecisionApplier

    private lateinit var sut: UpdateStudyReviewExtractionService

    private fun reviewerSut() = UpdateStudyReviewExtractionService(
        systematicStudyRepository,
        studyReviewRepository,
        credentialService,
        reviewerStudyRepository,
        majorityApplier
    )

    private lateinit var factory: TestDataFactory
    private lateinit var preconditionCheckerMocking: PreconditionCheckerMockingNew

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
        sut = UpdateStudyReviewExtractionService(
            systematicStudyRepository,
            studyReviewRepository,
            credentialService
        )
    }

    @Nested
    @Tag("ValidClasses")
    @DisplayName("When successfully updating a Study Review's extraction status")
    inner class WhenSuccessfullyUpdatingAStudyReviewExtraction {

        @Test
        fun `should successfully update a Study Review's extraction status`() {
            val dto = factory.generateDto()
            val request = factory.updateStatusRequestModel("INCLUDED", setOf("criteria1", "criteria2"))

            preconditionCheckerMocking.makeEverythingWork()

            every {
                studyReviewRepository.findById(request.systematicStudyId, request.studyReviewId.first())
            } returns dto

            sut.changeStatus(presenter, request)

            verify(exactly = 1) {
                studyReviewRepository.saveOrUpdate(any())
                presenter.prepareSuccessView(any())
            }
        }

        @Test
        fun `should update the reviewer's own copy and apply the majority`() {
            val request = factory.updateStatusRequestModel("INCLUDED", setOf("criteria1"))
                .copy(reviewerId = factory.researcherId)
            val studyId = request.studyReviewId.first()
            val copy = factory.generateReviewerStudyDto(studyId, factory.researcherId)

            preconditionCheckerMocking.makeEverythingWork()

            every {
                reviewerStudyRepository.findById(
                    request.systematicStudyId,
                    factory.researcherId,
                    studyId,
                    StudyReviewStage.EXTRACTION
                )
            } returns copy

            every { reviewerStudyRepository.saveOrUpdate(any()) } just Runs

            reviewerSut().changeStatus(presenter, request)

            verify(exactly = 1) {
                reviewerStudyRepository.saveOrUpdate(any())
                majorityApplier.apply(
                    request.systematicStudyId,
                    studyId,
                    StudyReviewStage.EXTRACTION
                )
                presenter.prepareSuccessView(any())
            }

            verify(exactly = 0) {
                studyReviewRepository.saveOrUpdate(any())
            }
        }

        @Test
        fun `should keep updating the original study and not apply the majority when no reviewer is informed`() {
            val dto = factory.generateDto()
            val request = factory.updateStatusRequestModel("INCLUDED", setOf("criteria1"))

            preconditionCheckerMocking.makeEverythingWork()

            every {
                studyReviewRepository.findById(request.systematicStudyId, request.studyReviewId.first())
            } returns dto

            reviewerSut().changeStatus(presenter, request)

            verify(exactly = 1) {
                studyReviewRepository.saveOrUpdate(any())
            }

            verify(exactly = 0) {
                majorityApplier.apply(any(), any(), any())
            }
        }
    }

    @Nested
    @Tag("InvalidClasses")
    @DisplayName("When failing to update a Study Review's extraction status")
    inner class WhenFailingToUpdateAStudyReviewExtraction {

        @Test
        fun `should not be able to update a non-existent study`() {
            val request = factory.updateStatusRequestModel("INCLUDED", setOf("criteria1", "criteria2"))

            preconditionCheckerMocking.makeEverythingWork()

            every {
                studyReviewRepository.findById(request.systematicStudyId, request.studyReviewId.first())
            } returns null

            sut.changeStatus(presenter, request)

            verify {
                presenter.prepareFailView(any<EntityNotFoundException>())
            }
        }

        @Test
        fun `should not let a reviewer classify another reviewer's copy`() {
            val request = factory.updateStatusRequestModel("INCLUDED", setOf("criteria1"))
                .copy(reviewerId = UUID.randomUUID())

            preconditionCheckerMocking.makeEverythingWork()

            reviewerSut().changeStatus(presenter, request)

            verify(exactly = 1) {
                presenter.prepareFailView(any<UnauthorizedUserException>())
            }

            verify(exactly = 0) {
                reviewerStudyRepository.saveOrUpdate(any())
            }

            verify(exactly = 0) {
                majorityApplier.apply(any(), any(), any())
            }
        }

        @Test
        fun `should not find a copy that was not assigned to the reviewer`() {
            val request = factory.updateStatusRequestModel("INCLUDED", setOf("criteria1"))
                .copy(reviewerId = factory.researcherId)

            preconditionCheckerMocking.makeEverythingWork()

            every {
                reviewerStudyRepository.findById(any(), any(), any(), any())
            } returns null

            reviewerSut().changeStatus(presenter, request)

            verify(exactly = 1) {
                presenter.prepareFailView(any<EntityNotFoundException>())
            }

            verify(exactly = 0) {
                majorityApplier.apply(any(), any(), any())
            }
        }

        @Test
        fun `should not accept duplicated as a new status`() {
            val dto = factory.generateDto()
            val request = factory.updateStatusRequestModel("DUPLICATED", setOf("criteria1", "criteria2"))

            preconditionCheckerMocking.makeEverythingWork()

            every {
                studyReviewRepository.findById(request.systematicStudyId, request.studyReviewId.first())
            } returns dto

            sut.changeStatus(presenter, request)

            verify {
                presenter.prepareFailView(any<IllegalArgumentException>())
            }
        }

        @Test
        fun `should not accept invalid statuses`() {
            val dto = factory.generateDto()
            val request = factory.updateStatusRequestModel("NOTREAL", setOf("criteria1", "criteria2"))

            preconditionCheckerMocking.makeEverythingWork()

            every {
                studyReviewRepository.findById(request.systematicStudyId, request.studyReviewId.first())
            } returns dto

            assertFailsWith<IllegalArgumentException> {
                sut.changeStatus(presenter, request)
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
        fun `should not update when systematic study does not exist`() {
            val request = factory.updateStatusRequestModel("status!", setOf("criteria1", "criteria2"))

            preconditionCheckerMocking.testForNonexistentSystematicStudy(presenter, request) { _, _ ->
                sut.changeStatus(presenter, request)
            }
        }
    }
}