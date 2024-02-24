import br.all.application.researcher.credentials.ResearcherCredentialsService
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.search.CreateSearchSessionServiceImpl
import br.all.application.search.create.CreateSearchSessionPresenter
import br.all.application.search.create.CreateSearchSessionService.*
import br.all.application.search.create.TestDataFactory
import br.all.application.search.repository.SearchSessionRepository
import br.all.application.search.repository.toDto
import br.all.application.shared.exceptions.*
import br.all.application.study.repository.StudyReviewRepository
import br.all.domain.model.protocol.ProtocolId
import br.all.domain.model.protocol.SearchSource
import br.all.domain.model.researcher.ResearcherId
import br.all.domain.model.review.SystematicStudyId
import br.all.domain.model.search.SearchSession
import br.all.domain.model.search.SearchSessionID
import br.all.domain.services.BibtexConverterService
import br.all.domain.services.UuidGeneratorService
import io.mockk.*
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.mock.web.MockMultipartFile
import org.springframework.web.multipart.MultipartFile
import java.io.ByteArrayInputStream
import java.time.LocalDateTime
import java.util.*
import javax.security.sasl.AuthenticationException
import kotlin.test.Test

object MultipartFileUtil {
    fun createMockMultipartFile(name: String, content: ByteArray): MultipartFile {
        return MockMultipartFile(name, ByteArrayInputStream(content))
    }
}

@ExtendWith(MockKExtension::class)
class CreateSearchSessionServiceImplTest {

    @MockK
    private lateinit var searchSessionRepository: SearchSessionRepository

    @MockK
    private lateinit var systematicStudyRepository: SystematicStudyRepository

    @MockK
    private lateinit var uuidGeneratorService: UuidGeneratorService

    @MockK
    private lateinit var bibtexConverterService: BibtexConverterService

    @MockK
    private lateinit var studyReviewRepository: StudyReviewRepository

    @MockK
    private lateinit var credentialsService: ResearcherCredentialsService

    private lateinit var sut: CreateSearchSessionServiceImpl
    private lateinit var testDataFactory: TestDataFactory

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
    }

    @Nested
    @DisplayName("Creation Success")
    inner class SuccessfulTests {
        @Test
        fun `createSession should create search session`() {
            val presenter = mockk<CreateSearchSessionPresenter>(relaxed = true)
            val request = RequestModel(
                researcherId = UUID.randomUUID(),
                systematicStudyId = UUID.randomUUID(),
                searchString = "Test search",
                additionalInfo = "Additional info",
                source = "Test source",
            )

            val systematicStudyId = SystematicStudyId(request.systematicStudyId)
            val protocolId = ProtocolId(request.systematicStudyId)
            val sessionId = SearchSessionID(UUID.randomUUID())
            val searchSession = SearchSession(
                sessionId, systematicStudyId, request.searchString, request.additionalInfo ?: "",
                LocalDateTime.now(), SearchSource(request.source)
            )

            every { credentialsService.isAuthenticated(ResearcherId(request.researcherId)) } returns true
            every { credentialsService.hasAuthority(ResearcherId(request.researcherId)) } returns true
            every { systematicStudyRepository.existsById(systematicStudyId.value()) } returns true
            every {
                systematicStudyRepository.hasReviewer(
                    systematicStudyId.value(),
                    request.researcherId
                )
            } returns true

            every { uuidGeneratorService.next() } returns sessionId.value

            every { systematicStudyRepository.findById(request.systematicStudyId) } returns mockk()
            every {
                searchSessionRepository.existsBySearchSource(
                    protocolId.value,
                    request.source
                )
            } returns false
            every { searchSessionRepository.create(searchSession.toDto()) } just Runs
            every { bibtexConverterService.convertManyToStudyReview(systematicStudyId, any()) } returns emptyList()
            every { studyReviewRepository.saveOrUpdateBatch(emptyList()) } returns emptyList()

            sut.createSession(presenter, request, testDataFactory.bibFileContent())

            verify {
                presenter.prepareSuccessView(any<ResponseModel>())
            }
        }
    }

    @Nested
    @DisplayName("Creation Failure")
    inner class FailureTests {
        @Test
        fun `createSession should fail authentication`() {
            val presenter = mockk<CreateSearchSessionPresenter>(relaxed = true)
            val request = RequestModel(
                researcherId = UUID.randomUUID(),
                systematicStudyId = UUID.randomUUID(),
                searchString = "Test search",
                additionalInfo = "Additional info",
                source = "Test source",
            )

            val systematicStudyId = SystematicStudyId(request.systematicStudyId)
            val protocolId = ProtocolId(request.systematicStudyId)
            val sessionId = SearchSessionID(UUID.randomUUID())
            val searchSession = SearchSession(
                sessionId, systematicStudyId, request.searchString, request.additionalInfo ?: "",
                LocalDateTime.now(), SearchSource(request.source)
            )

            every { credentialsService.isAuthenticated(ResearcherId(request.researcherId)) } returns false
            every { credentialsService.hasAuthority(ResearcherId(request.researcherId)) } returns true
            every { systematicStudyRepository.existsById(systematicStudyId.value()) } returns true
            every {
                systematicStudyRepository.hasReviewer(
                    systematicStudyId.value(),
                    request.researcherId
                )
            } returns true

            every { uuidGeneratorService.next() } returns sessionId.value

            every { systematicStudyRepository.findById(request.systematicStudyId) } returns mockk()
            every {
                searchSessionRepository.existsBySearchSource(
                    protocolId.value,
                    request.source
                )
            } returns false
            every { searchSessionRepository.create(searchSession.toDto()) } just Runs
            every { bibtexConverterService.convertManyToStudyReview(systematicStudyId, any()) } returns emptyList()
            every { studyReviewRepository.saveOrUpdateBatch(emptyList()) } returns emptyList()

            sut.createSession(presenter, request, testDataFactory.bibFileContent())

            verify {
                presenter.prepareFailView(match { it is UnauthenticatedUserException })
            }
        }

        @Test
        fun `createSession should fail due to inadequate permissions`() {
            val presenter = mockk<CreateSearchSessionPresenter>(relaxed = true)
            val bibFileContent = "bib_file_content".toByteArray()
            val mockMultipartFile = MultipartFileUtil.createMockMultipartFile("bibFile", bibFileContent)
            val request = RequestModel(
                researcherId = UUID.randomUUID(),
                systematicStudyId = UUID.randomUUID(),
                searchString = "Test search",
                additionalInfo = "Additional info",
                source ="Test source",
            )

            val systematicStudyId = SystematicStudyId(request.systematicStudyId)
            val protocolId = ProtocolId(request.systematicStudyId)
            val sessionId = SearchSessionID(UUID.randomUUID())
            val searchSession = SearchSession(
                sessionId, systematicStudyId, request.searchString, request.additionalInfo ?: "",
                LocalDateTime.now(), SearchSource(request.source)
            )

            every { credentialsService.isAuthenticated(ResearcherId(request.researcherId)) } returns true
            every { credentialsService.hasAuthority(ResearcherId(request.researcherId)) } returns false
            every { systematicStudyRepository.existsById(systematicStudyId.value()) } returns true
            every {
                systematicStudyRepository.hasReviewer(
                    systematicStudyId.value(),
                    request.researcherId
                )
            } returns true

            every { uuidGeneratorService.next() } returns sessionId.value

            every { systematicStudyRepository.findById(request.systematicStudyId) } returns mockk()
            every {
                searchSessionRepository.existsBySearchSource(
                    protocolId.value,
                    request.source
                )
            } returns false
            every { searchSessionRepository.create(searchSession.toDto()) } just Runs
            every { bibtexConverterService.convertManyToStudyReview(systematicStudyId, any()) } returns emptyList()
            every { studyReviewRepository.saveOrUpdateBatch(emptyList()) } returns emptyList()

            sut.createSession(presenter, request, testDataFactory.bibFileContent())

            verify {
                presenter.prepareFailView(match { it is UnauthorizedUserException })
            }
        }

        @Test
        fun `createSession should fail due to non-existent systematic study`() {
            val presenter = mockk<CreateSearchSessionPresenter>(relaxed = true)
            val bibFileContent = "bib_file_content".toByteArray()
            val mockMultipartFile = MultipartFileUtil.createMockMultipartFile("bibFile", bibFileContent)
            val request = RequestModel(
                researcherId = UUID.randomUUID(),
                systematicStudyId = UUID.randomUUID(),
                searchString = "Test search",
                additionalInfo = "Additional info",
                source ="Test source",
            )

            val systematicStudyId = SystematicStudyId(request.systematicStudyId)
            val protocolId = ProtocolId(request.systematicStudyId)
            val sessionId = SearchSessionID(UUID.randomUUID())
            val searchSession = SearchSession(
                sessionId, systematicStudyId, request.searchString, request.additionalInfo ?: "",
                LocalDateTime.now(), SearchSource(request.source)
            )

            every { credentialsService.isAuthenticated(ResearcherId(request.researcherId)) } returns true
            every { credentialsService.hasAuthority(ResearcherId(request.researcherId)) } returns true
            every { systematicStudyRepository.existsById(systematicStudyId.value()) } returns false

            every { uuidGeneratorService.next() } returns sessionId.value

            every { systematicStudyRepository.findById(request.systematicStudyId) } returns null
            every {
                searchSessionRepository.existsBySearchSource(
                    protocolId.value,
                    request.source
                )
            } returns false
            every { searchSessionRepository.create(searchSession.toDto()) } just Runs
            every { bibtexConverterService.convertManyToStudyReview(systematicStudyId, any()) } returns emptyList()
            every { studyReviewRepository.saveOrUpdateBatch(emptyList()) } returns emptyList()

            sut.createSession(presenter, request, testDataFactory.bibFileContent())

            verify {
                presenter.prepareFailView(match { it is EntityNotFoundException })
            }
        }

        @Test
        fun `createSession should fail due to existing search session for the same study and source`() {
            val presenter = mockk<CreateSearchSessionPresenter>(relaxed = true)
            val bibFileContent = "bib_file_content".toByteArray()
            val mockMultipartFile = MultipartFileUtil.createMockMultipartFile("bibFile", bibFileContent)
            val request = RequestModel(
                researcherId = UUID.randomUUID(),
                systematicStudyId = UUID.randomUUID(),
                searchString = "Test search",
                additionalInfo = "Additional info",
                source ="Test source",
            )

            val systematicStudyId = SystematicStudyId(request.systematicStudyId)
            val protocolId = ProtocolId(request.systematicStudyId)
            val sessionId = SearchSessionID(UUID.randomUUID())
            val searchSession = SearchSession(
                sessionId, systematicStudyId, request.searchString, request.additionalInfo ?: "",
                LocalDateTime.now(), SearchSource(request.source)
            )

            every { credentialsService.isAuthenticated(ResearcherId(request.researcherId)) } returns true
            every { credentialsService.hasAuthority(ResearcherId(request.researcherId)) } returns true
            every { systematicStudyRepository.existsById(systematicStudyId.value()) } returns true
            every {
                systematicStudyRepository.hasReviewer(
                    systematicStudyId.value(),
                    request.researcherId
                )
            } returns true

            every { uuidGeneratorService.next() } returns sessionId.value

            every { systematicStudyRepository.findById(request.systematicStudyId) } returns mockk()
            every {
                searchSessionRepository.existsBySearchSource(
                    protocolId.value,
                    request.source
                )
            } returns true
            every { searchSessionRepository.create(searchSession.toDto()) } just Runs
            every { bibtexConverterService.convertManyToStudyReview(systematicStudyId, any()) } returns emptyList()
            every { studyReviewRepository.saveOrUpdateBatch(emptyList()) } returns emptyList()

            // Act
            sut.createSession(presenter, request, testDataFactory.bibFileContent())

            verify {
                presenter.prepareFailView(match { it is UniquenessViolationException })
            }

        }
    }


    /*var systematicStudy = UUID.randomUUID();

    @Ignore
    @Test
    fun `Should create search session`() {
        val requestModel = SearchSessionRequestModel(
            systematicStudy,
            source = SearchSource("Example source"),
            searchString = "Search string",
            additionalInfo = "Additional information"
        )
        val sessionId = UUID.randomUUID()
        val protocolId = UUID.randomUUID()

        every { idGenerator.next() } returns sessionId
        every { repository.create(any())} returns Unit

        sut.createSession(requestModel)

        verify(exactly = 1) { repository.create(SearchSession.fromRequestModel(SearchSessionID(sessionId), ProtocolId(protocolId), requestModel)) }
    }*/
}