package br.all.application.study.find

import br.all.application.researcher.credentials.ResearcherCredentialsService
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.study.find.presenter.FindAllStudyReviewsPresenter
import br.all.application.study.find.service.FindAllStudyReviewsServiceImpl
import br.all.application.study.repository.StudyReviewRepository
import br.all.application.study.util.TestDataFactory
import br.all.application.util.PreconditionCheckerMocking
import io.mockk.*
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
@Tag("UnitTest")
@Tag("ServiceTest")
@ExtendWith(MockKExtension::class)
class FindAllStudyReviewsServiceImplTest {

    @MockK
    private lateinit var studyReviewRepository: StudyReviewRepository
    @MockK
    private lateinit var systematicStudyRepository: SystematicStudyRepository
    @MockK
    private lateinit var credentialService: ResearcherCredentialsService
    @MockK(relaxed = true)
    private lateinit var presenter: FindAllStudyReviewsPresenter

    private lateinit var sut: FindAllStudyReviewsServiceImpl

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
        sut = FindAllStudyReviewsServiceImpl(
            systematicStudyRepository,
            studyReviewRepository,
            credentialService,
        )
    }

    @Nested
    @Tag("ValidClasses")
    @DisplayName("When successfully finding study reviews")
    inner class WhenSuccessfullyFindingStudyReviews {
        @BeforeEach
        fun setUp() = run { preconditionCheckerMocking.makeEverythingWork() }
        @Test
        fun `should successfully find a singular study review`() {
            val request = factory.findAllRequestModel()
            val response = factory.findAllResponseModel(1)

            every { studyReviewRepository.findAllFromReview(request.systematicStudyId) } returns response.studyReviews

            sut.findAllFromReview(presenter, request)

            verify(exactly = 1) {
                presenter.prepareSuccessView(response)
            }
        }

        @Test
        fun `should successfully find all reviews`(){
            val request = factory.findAllRequestModel()
            val response = factory.findAllResponseModel(3)

            every { studyReviewRepository.findAllFromReview(request.systematicStudyId) } returns response.studyReviews

            sut.findAllFromReview(presenter, request)

            verify { presenter.prepareSuccessView(response) }
        }
    }

    @Nested
    @Tag("InvalidClasses")
    @DisplayName("When failing to find study reviews")
    inner class WhenFailingToFindStudyReviews {
        @Test
        fun `should not find any study reviews`() {
            val request = factory.findAllRequestModel()

            preconditionCheckerMocking.makeEverythingWork()
            every {studyReviewRepository.findAllFromReview(factory.systematicStudyId)} returns emptyList()

            sut.findAllFromReview(presenter, request)

            verify(exactly = 1) {
                presenter.prepareSuccessView(any())
            }
        }
    }

}
