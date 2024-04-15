package br.all.application.study.find

import br.all.application.researcher.credentials.ResearcherCredentialsService
import br.all.application.review.find.services.FindOneSystematicStudyServiceImpl
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.shared.exceptions.EntityNotFoundException
import br.all.application.shared.exceptions.UnauthenticatedUserException
import br.all.application.shared.exceptions.UnauthorizedUserException
import br.all.application.study.find.presenter.FindStudyReviewPresenter
import br.all.application.study.find.service.FindStudyReviewServiceImpl
import br.all.application.study.repository.StudyReviewRepository
import br.all.application.study.util.TestDataFactory
import br.all.application.util.PreconditionCheckerMocking
import br.all.domain.services.IdGeneratorService
import io.mockk.*
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
@Tag("UnitTest")
@Tag("ServiceTest")
@ExtendWith(MockKExtension::class)
class FindOneStudyReviewServiceImplTest {

    @MockK private lateinit var studyReviewRepository: StudyReviewRepository
    @MockK private lateinit var systematicStudyRepository: SystematicStudyRepository
    @MockK private lateinit var credentialService: ResearcherCredentialsService
    @MockK(relaxed = true) private lateinit var presenter: FindStudyReviewPresenter

    private lateinit var sut: FindStudyReviewServiceImpl

    private lateinit var factory: TestDataFactory
    private lateinit var preconditionCheckerMocking: PreconditionCheckerMocking

    @BeforeEach
    fun setUp() {
        factory = TestDataFactory()
        preconditionCheckerMocking = PreconditionCheckerMocking(
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
    inner class WhenFailingToCreateAStudyReview {
        @Test
        fun `should not find any study reviews`() {
            val request = factory.findRequestModel()

            preconditionCheckerMocking.makeEverythingWork()
            every { presenter.isDone() } returns false
            every {studyReviewRepository.findById(any(),any())} returns null

            sut.findOne(presenter, request)

            verify(exactly = 1) {
                presenter.prepareFailView(any())
            }
        }
    }

}