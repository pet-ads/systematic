package br.all.application.protocol.find

import br.all.application.protocol.repository.ProtocolRepository
import br.all.application.protocol.util.TestDataFactory
import br.all.application.researcher.credentials.ResearcherCredentialsService
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.shared.exceptions.EntityNotFoundException
import br.all.application.shared.exceptions.UnauthenticatedUserException
import br.all.application.shared.exceptions.UnauthorizedUserException
import br.all.application.util.PreconditionCheckerMocking
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import io.mockk.verifyOrder
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith

@Tag("UnitTest")
@Tag("ServiceTest")
@ExtendWith(MockKExtension::class)
class FindOneProtocolServiceImplTest {
    @MockK(relaxUnitFun = true)
    private lateinit var systematicStudyRepository: SystematicStudyRepository
    @MockK(relaxUnitFun = true)
    private lateinit var protocolRepository: ProtocolRepository
    @MockK
    private lateinit var credentialsService: ResearcherCredentialsService
    @MockK(relaxed = true)
    private lateinit var presenter: FindOneProtocolPresenter
    @InjectMockKs
    private lateinit var sut: FindOneProtocolServiceImpl

    private lateinit var factory: TestDataFactory
    private lateinit var preconditionCheckerMocking: PreconditionCheckerMocking

    @BeforeEach
    fun setUp() {
        factory = TestDataFactory()
        preconditionCheckerMocking = PreconditionCheckerMocking(
            presenter,
            credentialsService,
            systematicStudyRepository,
            factory.researcher,
            factory.systematicStudy,
        )
    }

    @Nested
    @Tag("ValidClasses")
    @DisplayName("When being able to find protocols")
    inner class WhenBeingAbleToFindProtocols {
        @Test
        fun `should find a existent protocol`() {
            val (_, protocolId) = factory
            val request = factory.findRequestModel()
            val dto = factory.protocolDto()
            val response = factory.findResponseModel(dto = dto)

            preconditionCheckerMocking.makeEverythingWork()
            every { protocolRepository.findById(protocolId) } returns dto

            sut.findById(presenter, request)

            verify {
                protocolRepository.findById(protocolId)
                presenter.prepareSuccessView(response)
            }
        }
    }

    @Nested
    @Tag("InvalidClasses")
    @DisplayName("When being unable to find any protocol")
    inner class WhenBeingUnableToFindAnyProtocol {
        @Test
        fun `should not be possible to find nonexistent protocols`() {
            val (_, protocolId) = factory
            val request = factory.findRequestModel()

            preconditionCheckerMocking.makeEverythingWork()
            every { protocolRepository.findById(protocolId) } returns null

            sut.findById(presenter, request)

            verify {
                protocolRepository.findById(protocolId)
                presenter.prepareFailView(any<EntityNotFoundException>())
            }
        }

        @Test
        fun `should prepare fail view when trying to find the protocol of a nonexistent study`() {
            val request = factory.findRequestModel()

            preconditionCheckerMocking.makeSystematicStudyNonexistent()
            sut.findById(presenter, request)

            verify {
                presenter.prepareFailView(any<EntityNotFoundException>())
                presenter.isDone()
            }
        }

        @Test
        fun `should not a non collaborator find protocols of a systematic study`() {
            val request = factory.findRequestModel()

            preconditionCheckerMocking.makeResearcherNotACollaborator()
            sut.findById(presenter, request)

            verifyOrder {
                presenter.prepareFailView(any<UnauthorizedUserException>())
                presenter.isDone()
            }
        }

        @Test
        fun `should prepare fail view for unauthenticated researchers`() {
            val request = factory.findRequestModel()

            preconditionCheckerMocking.makeResearcherUnauthenticated()
            sut.findById(presenter, request)

            verifyOrder {
                presenter.prepareFailView(any<UnauthenticatedUserException>())
                presenter.isDone()
            }
        }

        @Test
        fun `should not allow unauthorized users to find protocols`() {
            val request = factory.findRequestModel()

            preconditionCheckerMocking.makeResearcherUnauthorized()
            sut.findById(presenter, request)

            verifyOrder {
                presenter.prepareFailView(any<UnauthorizedUserException>())
                presenter.isDone()
            }
        }
    }
}
