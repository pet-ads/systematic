package br.all.application.protocol.find

import br.all.application.collaboration.repository.CollaborationRepository
import br.all.application.protocol.repository.ProtocolRepository
import br.all.application.protocol.util.TestDataFactory
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.shared.exceptions.EntityNotFoundException
import br.all.application.shared.exceptions.UnauthenticatedUserException
import br.all.application.shared.exceptions.UnauthorizedUserException
import br.all.application.user.CredentialsService
import br.all.application.util.PreconditionCheckerMockingNew
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
class FindProtocolServiceImplTest {
    @MockK(relaxUnitFun = true)
    private lateinit var systematicStudyRepository: SystematicStudyRepository
    @MockK(relaxUnitFun = true)
    private lateinit var protocolRepository: ProtocolRepository
    @MockK
    private lateinit var credentialsService: CredentialsService
    @MockK(relaxed = true)
    private lateinit var presenter: FindProtocolPresenter
    @MockK
    private lateinit var collaborationRepository: CollaborationRepository
    @InjectMockKs
    private lateinit var sut: FindProtocolServiceImpl

    private lateinit var factory: TestDataFactory
    private lateinit var preconditionCheckerMocking: PreconditionCheckerMockingNew

    @BeforeEach
    fun setUp() {
        factory = TestDataFactory()
        preconditionCheckerMocking = PreconditionCheckerMockingNew(
            presenter,
            credentialsService,
            systematicStudyRepository,
            collaborationRepository,
            factory.researcher,
            factory.systematicStudy,
            factory.collaboration
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

            verifyOrder {
                presenter.prepareFailView(any<EntityNotFoundException>())
                presenter.isDone()
            }
        }

        @Test
        fun `should prepare fail view for unauthenticated users`() {
            val request = factory.findRequestModel()

            preconditionCheckerMocking.makeUserUnauthenticated()
            sut.findById(presenter, request)

            verifyOrder {
                presenter.prepareFailView(any<UnauthenticatedUserException>())
                presenter.isDone()
            }
        }

        @Test
        fun `should not allow unauthorized users to find protocols`() {
            val request = factory.findRequestModel()

            preconditionCheckerMocking.makeUserUnauthorized()
            sut.findById(presenter, request)

            verifyOrder {
                presenter.prepareFailView(any<UnauthorizedUserException>())
                presenter.isDone()
            }
        }
    }
}
