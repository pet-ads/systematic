package br.all.application.protocol.update

import br.all.application.collaboration.repository.CollaborationRepository
import br.all.application.protocol.repository.ProtocolRepository
import br.all.application.protocol.util.TestDataFactory
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.shared.exceptions.EntityNotFoundException
import br.all.application.shared.exceptions.UnauthenticatedUserException
import br.all.application.shared.exceptions.UnauthorizedUserException
import br.all.application.study.repository.StudyReviewRepository
import br.all.application.user.CredentialsService
import br.all.application.util.PreconditionCheckerMockingNew
import br.all.domain.services.ScoreCalculatorService
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
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
    private lateinit var credentialsService: CredentialsService
    @MockK(relaxUnitFun = true)
    private lateinit var studyReviewRepository: StudyReviewRepository
    @MockK
    private lateinit var scoreCalculatorService: ScoreCalculatorService
    @MockK(relaxed = true)
    private lateinit var presenter: UpdateProtocolPresenter
    @MockK
    private lateinit var collaborationRepository: CollaborationRepository
    @InjectMockKs
    private lateinit var sut: UpdateProtocolServiceImpl

    private lateinit var factory: TestDataFactory
    private lateinit var preconditionCheckerMocking: PreconditionCheckerMockingNew

    @BeforeEach
    fun setUp() {
        factory = TestDataFactory()
        val (researcher, systematicStudy, collaboration) = factory
        preconditionCheckerMocking = PreconditionCheckerMockingNew(
            presenter,
            credentialsService,
            systematicStudyRepository,
            collaborationRepository,
            researcher,
            systematicStudy,
            collaboration
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
            val dto = factory.protocolDto()
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

        @Test
        fun `should not save when no updates are provided`() {
            val (_, systematicStudy) = factory
            val dto = factory.protocolDto()
            val request = factory.emptyUpdateRequest(dto)
            val response = factory.updateResponseModel()

            every { protocolRepository.findById(systematicStudy) } returns dto
            sut.update(presenter, request)

            verify(exactly = 0) { protocolRepository.saveOrUpdate(any()) }
            verify { presenter.prepareSuccessView(response) }
        }

        @Test
        fun `should create the protocol with the updates when it did not exist before`() {
            val (_, systematicStudy) = factory
            val request = factory.updateRequestModel()
            val response = factory.updateResponseModel()
            val dto = factory.protocolCreatedWithUpdates(request)

            every { protocolRepository.findById(systematicStudy) } returns null
            sut.update(presenter, request)

            verifyOrder {
                protocolRepository.saveOrUpdate(dto)
                presenter.prepareSuccessView(response)
            }
        }

        @Test
        fun `should recalculate scores when keywords are updated`() {
            val (_, systematicStudy) = factory
            val oldKeywords = setOf("old1", "old2")
            val newKeywords = setOf("new1", "new2")

            val oldDto = factory.protocolDto().copy(keywords = oldKeywords)

            val request = factory.updateRequestModel().copy(keywords = newKeywords)

            val updatedDto = factory.updatedDto(oldDto, request)

            every { protocolRepository.findById(systematicStudy) } returns oldDto
            every { studyReviewRepository.findAllFromReview(systematicStudy) } returns emptyList()
            every { scoreCalculatorService.applyScoreToManyStudyReviews(any(), newKeywords) } returns emptyList()

            sut.update(presenter, request)

            verify { 
                protocolRepository.saveOrUpdate(updatedDto)
                studyReviewRepository.findAllFromReview(systematicStudy)
                studyReviewRepository.saveOrUpdateBatch(any())
                presenter.prepareSuccessView(any())
            }
        }
    }

    @Nested
    @Tag("InvalidClasses")
    @DisplayName("When preconditions fail")
    inner class WhenPreconditionsFail {
        @Test
        fun `should prepare fail view when the systematic study does not exist`() {
            val request = factory.updateRequestModel()

            preconditionCheckerMocking.makeSystematicStudyNonexistent()
            sut.update(presenter, request)

            verifyOrder {
                presenter.prepareFailView(any<EntityNotFoundException>())
                presenter.isDone()
            }
        }

        @Test
        fun `should not allow unauthenticated users update any protocol`() {
            val request = factory.updateRequestModel()

            preconditionCheckerMocking.makeUserUnauthenticated()
            sut.update(presenter, request)

            verifyOrder {
                presenter.prepareFailView(any<UnauthenticatedUserException>())
                presenter.isDone()
            }
        }

        @Test
        fun `should not allow unauthorized users to update any protocol`() {
            val request = factory.updateRequestModel()

            preconditionCheckerMocking.makeUserUnauthorized()
            sut.update(presenter, request)

            verifyOrder {
                presenter.prepareFailView(any<UnauthorizedUserException>())
                presenter.isDone()
            }
        }
    }
}
