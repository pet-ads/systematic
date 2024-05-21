package br.all.application.review.find.services

import br.all.application.review.find.presenter.FindSystematicStudyPresenter
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.review.util.TestDataFactory
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
class FindSystematicStudyServiceImplTest {
    @MockK
    private lateinit var repository: SystematicStudyRepository
    @MockK
    private lateinit var credentialsService: CredentialsService
    @MockK(relaxed = true)
    private lateinit var presenter: FindSystematicStudyPresenter
    @InjectMockKs
    private lateinit var sut: FindSystematicStudyServiceImpl

    private lateinit var factory: TestDataFactory
    private lateinit var preconditionCheckerMocking: PreconditionCheckerMockingNew

    @BeforeEach
    fun setUp() {
        factory = TestDataFactory()
        preconditionCheckerMocking = PreconditionCheckerMockingNew(
            presenter,
            credentialsService,
            repository,
            factory.researcher,
            factory.systematicStudy,
        )
    }

    @Nested
    @Tag("ValidClasses")
    @DisplayName("When successfully finding one existent systematic study")
    inner class WhenSuccessfullyFindingOneExistentSystematicStudy {
        @Test
        fun `should correctly find a systematic study and prepare a success view`() {
            val (_, systematicStudy) = factory
            val request = factory.findOneRequestModel()
            val response = factory.findOneResponseModel()

            preconditionCheckerMocking.makeEverythingWork()
            every { repository.findById(systematicStudy) } returns response.content

            sut.findById(presenter, request)
            verify { presenter.prepareSuccessView(response) }
        }
    }

    @Nested
    @Tag("InvalidClasses")
    @DisplayName("When being unable to find a systematic study")
    inner class WhenBeingUnableToFindASystematicStudy {
        @Test
        fun `should prepare a fail view when trying to find a nonexistent systematic study`() {
            val request = factory.findOneRequestModel()

            preconditionCheckerMocking.makeSystematicStudyNonexistent()

            sut.findById(presenter, request)
            verifyOrder {
                presenter.prepareFailView(any<EntityNotFoundException>())
            }
        }

        @Test
        fun `should a unauthenticated researcher be unable to find any systematic study`() {
            val request = factory.findOneRequestModel()

            preconditionCheckerMocking.makeUserUnauthenticated()

            sut.findById(presenter, request)
            verifyOrder {
                presenter.prepareFailView(any<UnauthenticatedUserException>())
                presenter.isDone()
            }
        }

        @Test
        fun `should a unauthorized researcher be unable to find any systematic study`() {
            val request = factory.findOneRequestModel()

            preconditionCheckerMocking.makeUserUnauthorized()

            sut.findById(presenter, request)
            verifyOrder {
                presenter.prepareFailView(any<UnauthorizedUserException>())
                presenter.isDone()
            }
        }
    }
}
