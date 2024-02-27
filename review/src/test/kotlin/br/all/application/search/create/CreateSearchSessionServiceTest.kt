import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.search.CreateSearchSessionService
import br.all.application.search.create.SearchSessionRequestModel
import br.all.application.search.repository.SearchSessionRepository
import br.all.application.search.repository.fromRequestModel
import br.all.domain.model.protocol.ProtocolId
import br.all.domain.model.protocol.SearchSource
import br.all.domain.model.search.SearchSession
import br.all.domain.model.search.SearchSessionID
import br.all.domain.services.UuidGeneratorService
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import java.util.*
import kotlin.test.Ignore
import kotlin.test.Test

class CreateSearchSessionServiceTest {

    @MockK private lateinit var repository: SearchSessionRepository
    @MockK private lateinit var repositoryStudy: SystematicStudyRepository
    @MockK private lateinit var idGenerator: UuidGeneratorService

    private lateinit var sut: CreateSearchSessionService

    @BeforeEach
    fun setUp() {
        repository = mockk()
        repositoryStudy = mockk()
        idGenerator = mockk()
        sut = CreateSearchSessionService(repository, repositoryStudy, idGenerator)
    }

    var systematicStudy = UUID.randomUUID();

//    @Ignore
//    @Test
//    fun `Should create search session`() {
//        val requestModel = SearchSessionRequestModel(
//            systematicStudy,
//            source = SearchSource("Example source"),
//            searchString = "Search string",
//            additionalInfo = "Additional information"
//        )
//        val sessionId = UUID.randomUUID()
//        val protocolId = UUID.randomUUID()
//
//        every { idGenerator.next() } returns sessionId
//        every { repository.create(any())} returns Unit
//
//        sut.createSession(requestModel)
//
//        verify(exactly = 1) { repository.create(SearchSession.fromRequestModel(SearchSessionID(sessionId), ProtocolId(protocolId), requestModel)) }
//    }
}