package br.all.application.search.create

import br.all.application.protocol.repository.ProtocolRepository
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.search.util.TestDataFactory
import br.all.application.search.repository.SearchSessionRepository
import br.all.domain.shared.exception.EntityNotFoundException
import br.all.domain.shared.exception.UnauthenticatedUserException
import br.all.domain.shared.exception.UnauthorizedUserException
import br.all.application.study.repository.StudyReviewRepository
import br.all.application.user.CredentialsService
import br.all.application.util.PreconditionCheckerMockingNew
import br.all.domain.model.review.SystematicStudyId
import br.all.domain.model.search.SearchSessionID
import br.all.domain.services.ConverterFactoryService
import br.all.domain.services.ReviewSimilarityService
import br.all.domain.services.ScoreCalculatorService
import br.all.domain.services.UuidGeneratorService
import io.mockk.*
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.extension.ExtendWith
import kotlin.test.Test

@Tag("UnitTest")
@Tag("ServiceTest")
@ExtendWith(MockKExtension::class)
class CreateSearchSessionServiceImplTest {

    @MockK(relaxUnitFun = true)
    private lateinit var searchSessionRepository: SearchSessionRepository

    @MockK(relaxUnitFun = true)
    private lateinit var systematicStudyRepository: SystematicStudyRepository

    @MockK(relaxUnitFun = true)
    private lateinit var protocolRepository: ProtocolRepository

    @MockK
    private lateinit var uuidGeneratorService: UuidGeneratorService

    @MockK
    private lateinit var converterFactoryService: ConverterFactoryService

    @MockK(relaxUnitFun = true)
    private lateinit var studyReviewRepository: StudyReviewRepository

    @MockK
    private lateinit var credentialsService: CredentialsService

    @MockK
    private lateinit var scoreCalculatorService: ScoreCalculatorService

    @MockK
    private lateinit var reviewSimilarityService: ReviewSimilarityService

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
            protocolRepository,
            uuidGeneratorService,
            converterFactoryService,
            studyReviewRepository,
            credentialsService,
            scoreCalculatorService,
            reviewSimilarityService
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
            val request = testDataFactory.createRequestModel(source = testDataFactory.source)
            val response = testDataFactory.createResponseModel()
            val protocol = testDataFactory.generateProtocol()

            preconditionCheckerMocking.makeEverythingWork()
            every { protocolRepository.findById(systematicStudyUuid) } returns protocol
            every { uuidGeneratorService.next() } returns searchSessionId
            every { converterFactoryService.extractReferences(systematicStudyId, SearchSessionID(searchSessionId), any(), any()) } returns Pair(emptyList(), emptyList())
            every { scoreCalculatorService.applyScoreToManyStudyReviews(any(), any()) } returns emptyList()
            every { reviewSimilarityService.findDuplicates(any(), any()) } returns emptyMap()

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
            every { converterFactoryService.extractReferences(systematicStudyId, SearchSessionID(searchSessionId), any(), mutableSetOf("example source")) } returns Pair(emptyList(), emptyList())
            every { scoreCalculatorService.applyScoreToManyStudyReviews(any(), any()) } returns emptyList()
            every { reviewSimilarityService.findDuplicates(any(), any()) } returns emptyMap()

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
            every { converterFactoryService.extractReferences(systematicStudyId, SearchSessionID(searchSessionId), any(), mutableSetOf("example source")) } returns Pair(emptyList(), emptyList())
            every { scoreCalculatorService.applyScoreToManyStudyReviews(any(), any()) } returns emptyList()

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
            every { converterFactoryService.extractReferences(systematicStudyId, SearchSessionID(searchSessionId), any(), mutableSetOf("example source")) } returns Pair(emptyList(), emptyList())
            every { scoreCalculatorService.applyScoreToManyStudyReviews(any(), any()) } returns emptyList()

            sut.createSession(presenter, request, testDataFactory.bibFileContent())
            verify {presenter.prepareFailView(match { it is EntityNotFoundException }) }
        }
    }
}