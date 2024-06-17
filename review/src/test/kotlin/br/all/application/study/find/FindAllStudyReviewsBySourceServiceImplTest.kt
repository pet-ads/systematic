package br.all.application.study.find

import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.study.find.presenter.FindAllStudyReviewsBySourcePresenter
import br.all.application.study.find.service.FindAllStudyReviewsBySourceServiceImpl
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
class FindAllStudyReviewsBySourceServiceImplTest {

    @MockK
    private lateinit var studyReviewRepository: StudyReviewRepository
    @MockK
    private lateinit var systematicStudyRepository: SystematicStudyRepository
    @MockK
    private lateinit var credentialService: CredentialsService
    @MockK(relaxed = true)
    private lateinit var presenter: FindAllStudyReviewsBySourcePresenter

    private lateinit var sut: FindAllStudyReviewsBySourceServiceImpl

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
        sut = FindAllStudyReviewsBySourceServiceImpl(
            systematicStudyRepository,
            studyReviewRepository,
            credentialService,
        )
    }

    @Nested
    @Tag("ValidClasses")
    @DisplayName("When successfully finding study reviews from source")
    inner class WhenSuccessfullyFindingStudyReviewsFromSource {
        @BeforeEach
        fun setUp() = run { preconditionCheckerMocking.makeEverythingWork() }

        @Test
        fun `should successfully find a singular study review from the correct source`() {
            val searchSource = "testsource"
            val request = factory.findAllBySourceRequestModel(searchSource)
            val response = factory.findAllBySourceResponseModel(1, searchSource)

            preconditionCheckerMocking.makeEverythingWork()

            every {
                studyReviewRepository.findAllBySource(any(), any())
            } returns response.studyReviews

            sut.findAllFromSearchSession(presenter, request)

            verify(exactly = 1) {
                presenter.prepareSuccessView(response)
            }
        }

        @Test
        fun `should successfully find all reviews of search source`(){
            val searchSource = "testsource"
            val request = factory.findAllBySourceRequestModel(searchSource)
            val response = factory.findAllBySourceResponseModel(3, searchSource)

            preconditionCheckerMocking.makeEverythingWork()

            every {
                studyReviewRepository.findAllBySource(any(), any())
            } returns response.studyReviews

            sut.findAllFromSearchSession(presenter, request)

            verify {
                presenter.prepareSuccessView(response)
            }
        }
    }

    @Nested
    @Tag("InvalidClasses")
    @DisplayName("When failing to find study reviews")
    inner class WhenFailingToFindStudyReviewsFromSource {
        @Test
        fun `should not find any study reviews`() {
            val searchSource = "testsource"
            val request = factory.findAllBySourceRequestModel(searchSource)

            preconditionCheckerMocking.makeEverythingWork()
            every {studyReviewRepository.findAllBySource(any(),any())} returns emptyList()

            sut.findAllFromSearchSession(presenter, request)

            verify {
                presenter.prepareSuccessView(any())
            }
        }

        @Test
        fun `should not be allowed to find a study when unauthenticated`() {
            val request = factory.findAllBySourceRequestModel("false")

            preconditionCheckerMocking.testForUnauthenticatedUser(presenter, request) { _, _ ->
                sut.findAllFromSearchSession(presenter, request)
            }

        }

        @Test
        fun `should not be allowed to find a study when unauthorized`() {
            val request = factory.findAllBySourceRequestModel("no")

            preconditionCheckerMocking.testForUnauthorizedUser(presenter, request) { _, _ ->
                sut.findAllFromSearchSession(presenter, request)
            }

        }

        @Test
        fun `should not be allowed to find a study when systematic study does not exist`() {
            val request = factory.findAllBySourceRequestModel("nope")

            preconditionCheckerMocking.testForNonexistentSystematicStudy(presenter, request) { _, _ ->
                sut.findAllFromSearchSession(presenter, request)
            }

        }
    }

}

