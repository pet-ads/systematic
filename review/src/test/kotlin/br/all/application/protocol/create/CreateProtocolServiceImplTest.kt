package br.all.application.protocol.create

import br.all.application.protocol.create.CreateProtocolService.ResponseModel
import br.all.application.protocol.repository.ProtocolRepository
import br.all.application.protocol.repository.fromRequestModel
import br.all.application.protocol.repository.toDto
import br.all.application.protocol.util.TestDataFactory
import br.all.application.researcher.credentials.ResearcherCredentialsService
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.shared.presenter.PreconditionChecker
import br.all.domain.model.protocol.Protocol
import br.all.domain.model.researcher.ResearcherId
import br.all.domain.model.review.SystematicStudyId
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
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

    @BeforeEach
    fun setUp() {
        mockkConstructor(PreconditionChecker::class)
        factory = TestDataFactory()
    }

    @Test
    fun `should create a protocol for an existent systematic study`() {
        val (researcher, systematicStudy) = factory
        val request = factory.createRequestModel()
        val dto = Protocol.fromRequestModel(request).toDto()
        val response = ResponseModel(researcher, systematicStudy)

        every {
            anyConstructed<PreconditionChecker>().prepareIfViolatesPreconditions(
                presenter,
                ResearcherId(researcher), 
                SystematicStudyId(systematicStudy)
            )
        } just Runs

        sut.create(presenter, request)

        verify {
            repository.saveOrUpdate(dto)
            presenter.prepareSuccessView(response)
        }
    }
}
