
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.search.CreateSearchSessionServiceImpl
import br.all.application.search.create.CreateSearchSessionPresenter
import br.all.application.search.util.TestDataFactory
import br.all.application.search.repository.SearchSessionRepository
import br.all.application.shared.exceptions.EntityNotFoundException
import br.all.application.shared.exceptions.UnauthenticatedUserException
import br.all.application.shared.exceptions.UnauthorizedUserException
import br.all.application.study.repository.StudyReviewRepository
import br.all.application.user.CredentialsService
import br.all.application.util.PreconditionCheckerMockingNew
import br.all.domain.model.review.SystematicStudyId
import br.all.domain.services.BibtexConverterService
import br.all.domain.services.UuidGeneratorService
import io.mockk.*
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.extension.ExtendWith
import kotlin.test.Test

@ExtendWith(MockKExtension::class)
class CreateSearchSessionServiceImplTest {

    @MockK(relaxUnitFun = true)
    private lateinit var searchSessionRepository: SearchSessionRepository

    @MockK(relaxUnitFun = true)
    private lateinit var systematicStudyRepository: SystematicStudyRepository

    @MockK
    private lateinit var uuidGeneratorService: UuidGeneratorService

    @MockK
    private lateinit var bibtexConverterService: BibtexConverterService

    @MockK(relaxUnitFun = true)
    private lateinit var studyReviewRepository: StudyReviewRepository

    @MockK
    private lateinit var credentialsService: CredentialsService

    @MockK(relaxed = true)
    private lateinit var presenter: CreateSearchSessionPresenter

    private lateinit var sut: CreateSearchSessionServiceImpl
    private lateinit var testDataFactory: TestDataFactory
    private lateinit var preconditionCheckerMocking: PreconditionCheckerMockingNew


    @BeforeEach
    fun setUp() {
        sut = CreateSearchSessionServiceImpl(
            searchSessionRepository,
            systematicStudyRepository,
            uuidGeneratorService,
            bibtexConverterService,
            studyReviewRepository,
            credentialsService
        )
        testDataFactory = TestDataFactory()
        preconditionCheckerMocking = PreconditionCheckerMockingNew(
            presenter,
            credentialsService,
            systematicStudyRepository,
            testDataFactory.userId,
            testDataFactory.systematicStudyId,
        )
    }

    @Nested
    @DisplayName("Creation Success")
    inner class SuccessfulTests {
        @Test
        fun `createSession should create search session`() {
            val (_, systematicStudyUuid, searchSessionId) = testDataFactory
            val systematicStudyId = SystematicStudyId(systematicStudyUuid)
            val request = testDataFactory.createRequestModel()
            val response = testDataFactory.createResponseModel()

            preconditionCheckerMocking.makeEverythingWork()
            every {
                searchSessionRepository.existsBySearchSource(systematicStudyUuid, request.source)
            } returns false
            every { uuidGeneratorService.next() } returns searchSessionId
            every { bibtexConverterService.convertManyToStudyReview(systematicStudyId, any()) } returns emptyList()

            sut.createSession(presenter, request, testDataFactory.bibFileContent())

            verify {
                presenter.prepareSuccessView(response)
            }
        }
    }

    @Nested
    @DisplayName("Creation Failure")
    inner class FailureTests {
        @Test
        fun `createSession should fail authentication`() {
            val (_, systematicStudyUuid, searchSessionId) = testDataFactory
            val systematicStudyId = SystematicStudyId(systematicStudyUuid)
            val request = testDataFactory.createRequestModel()

            preconditionCheckerMocking.makeUserUnauthenticated()
            every {
                searchSessionRepository.existsBySearchSource(systematicStudyUuid, request.source)
            } returns false
            every { uuidGeneratorService.next() } returns searchSessionId
            every { bibtexConverterService.convertManyToStudyReview(systematicStudyId, any()) } returns emptyList()

            sut.createSession(presenter, request, testDataFactory.bibFileContent())

            verify { presenter.prepareFailView(match { it is UnauthenticatedUserException }) }
        }

        @Test
        fun `createSession should fail due to inadequate permissions`() {
            val (_, systematicStudyUuid, searchSessionId) = testDataFactory
            val systematicStudyId = SystematicStudyId(systematicStudyUuid)
            val request = testDataFactory.createRequestModel()

            preconditionCheckerMocking.makeUserUnauthorized()
            every {
                searchSessionRepository.existsBySearchSource(systematicStudyUuid, request.source)
            } returns false
            every { uuidGeneratorService.next() } returns searchSessionId
            every { bibtexConverterService.convertManyToStudyReview(systematicStudyId, any()) } returns emptyList()

            sut.createSession(presenter, request, testDataFactory.bibFileContent())

            verify { presenter.prepareFailView(match { it is UnauthorizedUserException }) }
        }

        @Test
        fun `createSession should fail due to non-existent systematic study`() {
            val (_, systematicStudyUuid, searchSessionId) = testDataFactory
            val systematicStudyId = SystematicStudyId(systematicStudyUuid)
            val request = testDataFactory.createRequestModel()

            preconditionCheckerMocking.makeSystematicStudyNonexistent()
            every {
                searchSessionRepository.existsBySearchSource(systematicStudyUuid, request.source)
            } returns false
            every { uuidGeneratorService.next() } returns searchSessionId
            every { bibtexConverterService.convertManyToStudyReview(systematicStudyId, any()) } returns emptyList()

            sut.createSession(presenter, request, testDataFactory.bibFileContent())
            verify {presenter.prepareFailView(match { it is EntityNotFoundException }) }
        }
    }
}