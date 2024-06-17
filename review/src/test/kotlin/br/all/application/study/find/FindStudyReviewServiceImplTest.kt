package br.all.application.study.find

import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.study.find.presenter.FindStudyReviewPresenter
import br.all.application.study.find.service.FindStudyReviewServiceImpl
import br.all.application.study.repository.StudyReviewRepository
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
class FindStudyReviewServiceImplTest {

    @MockK private lateinit var studyReviewRepository: StudyReviewRepository
    @MockK private lateinit var systematicStudyRepository: SystematicStudyRepository
    @MockK private lateinit var credentialService: CredentialsService
    @MockK(relaxed = true) private lateinit var presenter: FindStudyReviewPresenter

    private lateinit var sut: FindStudyReviewServiceImpl

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
        sut = FindStudyReviewServiceImpl(
            systematicStudyRepository,
            studyReviewRepository,
            credentialService,
        )
    }

    @Nested
    @Tag("ValidClasses")
    @DisplayName("When successfully finding a study review")
    inner class WhenSuccessfullyFindingAStudyReview {
        @Test
        fun `should successfully finding a Study Review`() {
            val request = factory.findRequestModel()
            val response = factory.findResponseModel()

            preconditionCheckerMocking.makeEverythingWork()
            every { studyReviewRepository.findById(any(),any())} returns response.content

            sut.findOne(presenter, request)

            verify(exactly = 1) {
                presenter.prepareSuccessView(response)
            }
        }
    }

    @Nested
    @Tag("InvalidClasses")
    @DisplayName("When failing to find a study review")
    inner class WhenFailingToFindAStudyReview {
        @Test
        fun `should not find any study reviews`() {
            val request = factory.findRequestModel()

            preconditionCheckerMocking.makeEverythingWork()
            every {studyReviewRepository.findById(any(),any())} returns null

            sut.findOne(presenter, request)

            verify(exactly = 1) {
                presenter.prepareFailView(any())
            }
        }

        @Test
        fun `should not be allowed to find a study when unauthenticated`() {
            val request = factory.findRequestModel()

            preconditionCheckerMocking.testForUnauthenticatedUser(presenter, request) { _, _ ->
                sut.findOne(presenter, request)
            }

        }

        @Test
        fun `should not be allowed to find a study when unauthorized`() {
            val request = factory.findRequestModel()

            preconditionCheckerMocking.testForUnauthorizedUser(presenter, request) { _, _ ->
                sut.findOne(presenter, request)
            }

        }

        @Test
        fun `should not be allowed to find a study when systematic study doesn't exist`() {
            val request = factory.findRequestModel()

            preconditionCheckerMocking.testForNonexistentSystematicStudy(presenter, request) { _, _ ->
                sut.findOne(presenter, request)
            }

        }
    }

}