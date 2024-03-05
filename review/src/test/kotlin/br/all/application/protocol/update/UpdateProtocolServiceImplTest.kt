package br.all.application.protocol.update

import br.all.application.protocol.repository.ProtocolRepository
import br.all.application.protocol.util.TestDataFactory
import br.all.application.researcher.credentials.ResearcherCredentialsService
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.util.PreconditionCheckerMocking
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verifyOrder
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith

@Tag("ServiceTest")
@Tag("UnitTest")
@ExtendWith(MockKExtension::class)
class UpdateProtocolServiceImplTest {
    @MockK(relaxUnitFun = true)
    private lateinit var protocolRepository: ProtocolRepository
    @MockK(relaxUnitFun = true)
    private lateinit var systematicStudyRepository: SystematicStudyRepository
    @MockK
    private lateinit var credentialsService: ResearcherCredentialsService
    @MockK(relaxed = true)
    private lateinit var presenter: UpdateProtocolPresenter
    @InjectMockKs
    private lateinit var sut: UpdateProtocolServiceImpl

    private lateinit var factory: TestDataFactory
    private lateinit var preconditionCheckerMocking: PreconditionCheckerMocking

    @BeforeEach
    fun setUp() {
        factory = TestDataFactory()
        val (researcher, systematicStudy) = factory
        preconditionCheckerMocking = PreconditionCheckerMocking(
            presenter,
            credentialsService,
            systematicStudyRepository,
            researcher,
            systematicStudy,
        )
    }

    @Nested
    @Tag("ValidClasses")
    @DisplayName("When being succeed to update")
    inner class WhenBeingSucceedToUpdate {
        @BeforeEach
        fun setUp() = run { preconditionCheckerMocking.makeEverythingWork() }

        @Test
        fun `should update a existent protocol`() {
            val (_, systematicStudy) = factory
            val dto = factory.createProtocolDto()
            val request = factory.updateRequestModel()
            val response = factory.updateResponseModel()
            val updatedDto = factory.updatedDto(dto, request)

            every { protocolRepository.findById(systematicStudy) } returns dto
            sut.update(presenter, request)

            verifyOrder {
                protocolRepository.saveOrUpdate(updatedDto)
                presenter.prepareSuccessView(response)
            }
        }
    }
}
