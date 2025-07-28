package br.all.application.study.update

import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.shared.exceptions.EntityNotFoundException
import br.all.application.study.repository.StudyReviewRepository
import br.all.application.study.update.implementation.UpdateStudyReviewServiceImpl
import br.all.application.study.update.interfaces.UpdateStudyReviewPresenter
import br.all.application.study.util.TestDataFactory
import br.all.application.user.CredentialsService
import br.all.application.util.PreconditionCheckerMockingNew
import io.mockk.*
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
@Tag("UnitTest")
@Tag("ServiceTest")
@ExtendWith(MockKExtension::class)
class UpdateStudyReviewServiceImplTest {

    @MockK(relaxed = true) private lateinit var studyReviewRepository: StudyReviewRepository
    @MockK(relaxUnitFun = true) private lateinit var systematicStudyRepository: SystematicStudyRepository
    @MockK private lateinit var credentialService: CredentialsService
    @MockK(relaxed = true) private lateinit var presenter: UpdateStudyReviewPresenter

    private lateinit var sut: UpdateStudyReviewServiceImpl

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
        sut = UpdateStudyReviewServiceImpl(
            systematicStudyRepository,
            studyReviewRepository,
            credentialService,
        )
    }

    @Nested
    @Tag("ValidClasses")
    @DisplayName("When successfully updating a Study Review")
    inner class WhenSuccessfullyUpdatingAStudyReview {
        @Test
        fun `should successfully update a Study Review in its entirety`() {
            val dto = factory.generateDto()
            val request = factory.updateRequestModel()

            preconditionCheckerMocking.makeEverythingWork()

            every { studyReviewRepository.findById(request.systematicStudyId, request.studyReviewId) } returns dto

            sut.updateFromStudy(presenter, request)

            verify(exactly = 1) {
                studyReviewRepository.saveOrUpdate(any())
                presenter.prepareSuccessView(any())
            }
        }
    }

    @Nested
    @Tag("InvalidClasses")
    @DisplayName("When failing to update a study review")
    inner class WhenFailingToUpdateAStudyReview {
        @Test
        fun `should not be able to update a non-existent study`() {
            val request = factory.updateRequestModel()

            preconditionCheckerMocking.makeEverythingWork()

            every { studyReviewRepository.findById(request.systematicStudyId, request.studyReviewId) } returns null
            sut.updateFromStudy(presenter, request)

            verify {
                presenter.prepareFailView(any<EntityNotFoundException>())
            }
        }

        @Test
        fun `should not update when unauthenticated`() {
            val request = factory.updateRequestModel()

            preconditionCheckerMocking.testForUnauthenticatedUser(presenter, request) { _, _ ->
                sut.updateFromStudy(presenter, request)
            }

        }

        @Test
        fun `should not update when unauthorized`() {
            val request = factory.updateRequestModel()

            preconditionCheckerMocking.testForUnauthorizedUser(presenter, request) { _, _ ->
                sut.updateFromStudy(presenter, request)
            }

        }

        @Test
        fun `should not update when systematic study`() {
            val request = factory.updateRequestModel()

            preconditionCheckerMocking.testForNonexistentSystematicStudy(presenter, request) { _, _ ->
                sut.updateFromStudy(presenter, request)
            }

        }
    }

}