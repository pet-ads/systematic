package br.all.application.study.create

import br.all.application.researcher.credentials.ResearcherCredentialsService
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.shared.exceptions.UnauthenticatedUserException
import br.all.application.shared.exceptions.UnauthorizedUserException
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
class CreateStudyReviewServiceImplTest {

    @MockK(relaxed = true) private lateinit var studyReviewRepository: StudyReviewRepository
    @MockK(relaxUnitFun = true) private lateinit var systematicStudyRepository: SystematicStudyRepository
    @MockK private lateinit var idGenerator: IdGeneratorService
    @MockK private lateinit var credentialService: ResearcherCredentialsService
    @MockK(relaxUnitFun = true) private lateinit var presenter: CreateStudyReviewPresenter

    private lateinit var sut: CreateStudyReviewServiceImpl

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
        sut = CreateStudyReviewServiceImpl(
            systematicStudyRepository,
            studyReviewRepository,
            credentialService,
            idGenerator
        )
    }

    @Nested
    @Tag("ValidClasses")
    @DisplayName("When successfully creating a Study Review")
    inner class WhenSuccessfullyCreatingAStudyReview {
        @Test
        fun `should successfully create a Study Review`() {
            val (_, studyReviewId) = factory
            val request = factory.createRequestModel()

            preconditionCheckerMocking.makeEverythingWork()
            every { idGenerator.next() } returns studyReviewId
            every { presenter.isDone() } returns false

            sut.createFromStudy(presenter, request)

            verify(exactly = 1) {
                idGenerator.next()
                studyReviewRepository.saveOrUpdate(any())
                presenter.prepareSuccessView(any())
            }
        }
    }

    @Nested
    @Tag("InvalidClasses")
    @DisplayName("When failing to create a study review")
    inner class WhenFailingToCreateAStudyReview {
        @Test
        fun `should not be allowed to create a new study when unauthenticated`() {
            val request = factory.createRequestModel()

            preconditionCheckerMocking.makeResearcherUnauthenticated()
            sut.createFromStudy(presenter, request)

            verifyOrder {
                presenter.prepareFailView(any<UnauthenticatedUserException>())
                presenter.isDone()
            }
        }

        @Test
        fun `should not be allowed to create a new study when unauthorized`() {
            val request = factory.createRequestModel()

            preconditionCheckerMocking.makeResearcherUnauthorized()
            sut.createFromStudy(presenter, request)

            verifyOrder {
                presenter.prepareFailView(any<UnauthorizedUserException>())
                presenter.isDone()
            }
        }
    }

}