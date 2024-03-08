package br.all.application.review.create

import br.all.application.protocol.repository.ProtocolRepository
import br.all.application.researcher.credentials.ResearcherCredentialsService
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.review.util.TestDataFactory
import br.all.application.shared.exceptions.UnauthenticatedUserException
import br.all.application.shared.exceptions.UnauthorizedUserException
import br.all.application.util.PreconditionCheckerMocking
import br.all.domain.services.UuidGeneratorService
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith

@Tag("UnitTest")
@Tag("ServiceTest")
@ExtendWith(MockKExtension::class)
class CreateSystematicStudyServiceImplTest {
    @MockK(relaxUnitFun = true)
    private lateinit var systematicStudyRepository: SystematicStudyRepository
    @MockK(relaxUnitFun = true)
    private lateinit var protocolRepository: ProtocolRepository
    @MockK
    private lateinit var uuidGeneratorService: UuidGeneratorService
    @MockK
    private lateinit var credentialsService: ResearcherCredentialsService
    @MockK(relaxed = true)
    private lateinit var presenter: CreateSystematicStudyPresenter

    private lateinit var factory: TestDataFactory
    private lateinit var preconditionCheckerMocking: PreconditionCheckerMocking
    @InjectMockKs
    private lateinit var sut: CreateSystematicStudyServiceImpl

    @BeforeEach
    fun setUp() {
        factory = TestDataFactory()
        preconditionCheckerMocking = PreconditionCheckerMocking(
            presenter,
            credentialsService,
            systematicStudyRepository,
            factory.researcher,
            factory.systematicStudy
        )
    }

    @Nested
    @Tag("ValidClasses")
    @DisplayName("When successfully creating a Systematic Study")
    inner class WhenSuccessfullyCreatingASystematicStudy {
        @Test
        fun `should successfully create a systematic study`() {
            val (researcher, systematicStudy) = factory
            val request = factory.createRequestModel()
            val response = factory.createResponseModel()
            val dto = factory.dtoFromCreateRequest(request)
            val protocolDto = factory.protocolDto()

            preconditionCheckerMocking.makeEverythingWork()
            every { uuidGeneratorService.next() } returns systematicStudy

            sut.create(presenter, researcher, request)

            verify(exactly = 1) {
                uuidGeneratorService.next()
                systematicStudyRepository.saveOrUpdate(dto)
                protocolRepository.saveOrUpdate(protocolDto)
                presenter.prepareSuccessView(response)
            }
        }
    }

    @Nested
    @Tag("InvalidClasses")
    @DisplayName("When unable to create a new Systematic Study")
    inner class WhenUnableToCreateANewSystematicStudy {
        @Test
        fun `should not the researcher be allowed to create a new study when unauthenticated`() {
            val (researcher) = factory
            val request = factory.createRequestModel()

            preconditionCheckerMocking.makeResearcherUnauthenticated()
            sut.create(presenter, researcher, request)

            verify(exactly = 1) {
                presenter.prepareFailView(any<UnauthenticatedUserException>())
                presenter.isDone()
            }
        }

        @Test
        fun `should not the researcher be allowed to create a study when unauthorized`() {
            val (researcher) = factory
            val request = factory.createRequestModel()

            preconditionCheckerMocking.makeResearcherUnauthorized()
            sut.create(presenter, researcher, request)

            verify(exactly = 1) {
                presenter.prepareFailView(any<UnauthorizedUserException>())
                presenter.isDone()
            }
        }
    }
}
