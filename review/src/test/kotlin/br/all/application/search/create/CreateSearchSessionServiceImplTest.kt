import br.all.application.researcher.credentials.ResearcherCredentialsService
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.search.CreateSearchSessionServiceImpl
import br.all.application.search.create.CreateSearchSessionPresenter
import br.all.application.search.create.CreateSearchSessionService
import br.all.application.search.repository.SearchSessionRepository
import br.all.application.study.repository.StudyReviewRepository
import br.all.domain.model.protocol.ProtocolId
import br.all.domain.model.protocol.SearchSource
import br.all.domain.model.review.SystematicStudyId
import br.all.domain.model.search.SearchSession
import br.all.domain.model.search.SearchSessionID
import br.all.domain.services.BibtexConverterService
import br.all.domain.services.UuidGeneratorService
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.mock.web.MockMultipartFile
import org.springframework.web.multipart.MultipartFile
import java.io.ByteArrayInputStream
import java.time.LocalDateTime
import java.util.*
import kotlin.test.Test

object MultipartFileUtil {
    fun createMockMultipartFile(name: String, content: ByteArray): MultipartFile {
        return MockMultipartFile(name, ByteArrayInputStream(content))
    }
}

@ExtendWith(MockKExtension::class)
class CreateSearchSessionServiceImplTest {

    @MockK private lateinit var searchSessionRepository: SearchSessionRepository
    @MockK private lateinit var systematicStudyRepository: SystematicStudyRepository
    @MockK private lateinit var uuidGeneratorService: UuidGeneratorService
    @MockK private lateinit var bibtexConverterService: BibtexConverterService
    @MockK private lateinit var studyReviewRepository: StudyReviewRepository
    @MockK private lateinit var credentialsService: ResearcherCredentialsService

    private lateinit var sut: CreateSearchSessionServiceImpl

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
    }

    @Test
    fun `createSession should create search session`() {
        val presenter = mockk<CreateSearchSessionPresenter>()
        val bibFileContent = "bib_file_content".toByteArray()
        val mockMultipartFile = MultipartFileUtil.createMockMultipartFile("bibFile", bibFileContent)
        val request = CreateSearchSessionService.RequestModel(
            researcherId = UUID.randomUUID(),
            systematicStudyId = UUID.randomUUID(),
            searchString = "Test search",
            additionalInfo = "Additional info",
            source = SearchSource("Test source"),
            bibFile = mockMultipartFile
        )

        val systematicStudyId = SystematicStudyId(request.systematicStudyId)
        val protocolId = ProtocolId(request.systematicStudyId)
        val sessionId = SearchSessionID(UUID.randomUUID())
        val searchSession = SearchSession(sessionId, systematicStudyId, request.searchString, request.additionalInfo ?: "",
            LocalDateTime.now(), request.source)

        every { systematicStudyRepository.findById(request.systematicStudyId) } returns mockk()
        every { searchSessionRepository.getSearchSessionBySource(protocolId, request.source) } returns null
        every { uuidGeneratorService.next() } returns sessionId.value
        every { credentialsService.isAuthenticated(any()) } returns true
        every { bibtexConverterService.convertManyToStudyReview(systematicStudyId, any()) } returns emptyList()

        sut.createSession(presenter, request)

        verify { searchSessionRepository.create(searchSession) }
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