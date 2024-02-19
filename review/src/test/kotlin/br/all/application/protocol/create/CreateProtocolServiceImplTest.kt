package br.all.application.protocol.create

import br.all.application.protocol.create.CreateProtocolService.ResponseModel
import br.all.application.protocol.repository.ProtocolRepository
import br.all.application.protocol.repository.fromRequestModel
import br.all.application.protocol.repository.toDto
import br.all.application.protocol.util.TestDataFactory
import br.all.application.researcher.credentials.ResearcherCredentialsService
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.shared.exceptions.EntityNotFoundException
import br.all.application.shared.exceptions.UnauthenticatedUserException
import br.all.application.shared.exceptions.UnauthorizedUserException
import br.all.application.util.PreconditionCheckerMocking
import br.all.domain.model.protocol.Protocol
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@Tag("UnitTest")
@Tag("ServiceTest")
@ExtendWith(MockKExtension::class)
class CreateProtocolServiceImplTest {
    @MockK(relaxUnitFun = true)
    private lateinit var repository: ProtocolRepository
    @MockK(relaxUnitFun = true)
    private lateinit var systematicStudyRepository: SystematicStudyRepository
    @MockK
    private lateinit var researcherCredentialsService: ResearcherCredentialsService
    @MockK(relaxed = true)
    private lateinit var presenter: CreateProtocolPresenter
    @InjectMockKs
    private lateinit var sut: CreateProtocolServiceImpl

    private lateinit var factory: TestDataFactory
    private lateinit var preconditionCheckerMocking: PreconditionCheckerMocking

    @BeforeEach
    fun setUp() {
        factory = TestDataFactory()
        preconditionCheckerMocking = PreconditionCheckerMocking(
            presenter,
            researcherCredentialsService,
            systematicStudyRepository,
            factory.researcher,
            factory.systematicStudy,
        )
    }

    @Test
    fun `should create a protocol for an existent systematic study`() {
        val (researcher, systematicStudy) = factory
        val request = factory.createRequestModel()
        val dto = Protocol.fromRequestModel(request).toDto()
        val response = ResponseModel(researcher, systematicStudy)

        preconditionCheckerMocking.makeEverythingWork()

        sut.create(presenter, request)

        verify {
            repository.saveOrUpdate(dto)
            presenter.prepareSuccessView(response)
        }
    }

    @Test
    fun `should not be possible to write a protocol for a nonexistent study`() {
        val request = factory.createRequestModel()

        preconditionCheckerMocking.makeSystematicStudyNonexistent()
        sut.create(presenter, request)

        verify { presenter.prepareFailView(any<EntityNotFoundException>()) }
    }

    @Test
    fun `should throw when the researcher is not a collaborator of the requested study`() {
        val request = factory.createRequestModel()

        preconditionCheckerMocking.makeResearcherNotACollaborator()
        sut.create(presenter, request)

        verify { presenter.prepareFailView(any<UnauthorizedUserException>()) }
    }

    @Test
    fun `should throw when the researcher is unauthenticated`() {
        val request = factory.createRequestModel()

        preconditionCheckerMocking.makeResearcherUnauthenticated()
        sut.create(presenter, request)

        verify { presenter.prepareFailView(any<UnauthenticatedUserException>()) }
    }

    @Test
    fun `should throw when the researcher has no permission`() {
        val request = factory.createRequestModel()

        preconditionCheckerMocking.makeResearcherUnauthorized()
        sut.create(presenter, request)

        verify { presenter.prepareFailView(any<UnauthorizedUserException>()) }
    }
}
