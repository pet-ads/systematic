package br.all.application.review.find.services

import br.all.application.researcher.credentials.ResearcherCredentialsService
import br.all.application.review.find.presenter.FindOneSystematicStudyPresenter
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.review.util.TestDataFactory
import br.all.application.shared.exceptions.EntityNotFoundException
import br.all.application.shared.exceptions.UnauthenticatedUserException
import br.all.application.shared.exceptions.UnauthorizedUserException
import br.all.application.util.PreconditionCheckerMocking
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
class FindOneSystematicStudyServiceImplTest {
    @MockK
    private lateinit var repository: SystematicStudyRepository
    @MockK
    private lateinit var credentialsService: ResearcherCredentialsService
    @MockK(relaxed = true)
    private lateinit var presenter: FindOneSystematicStudyPresenter
    @InjectMockKs
    private lateinit var sut: FindOneSystematicStudyServiceImpl

    private lateinit var factory: TestDataFactory
    private lateinit var preconditionCheckerMocking: PreconditionCheckerMocking

    @BeforeEach
    fun setUp() {
        factory = TestDataFactory()
        preconditionCheckerMocking = PreconditionCheckerMocking(
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
            val (researcher, systematicStudy) = factory
            val response = factory.findOneResponseModel()

            preconditionCheckerMocking.makeEverythingWork()
            every { repository.findById(systematicStudy) } returns response.content

            sut.findById(presenter, researcher, systematicStudy)
            verify { presenter.prepareSuccessView(response) }
        }
    }

    @Nested
    @Tag("InvalidClasses")
    @DisplayName("When being unable to find a systematic study")
    inner class WhenBeingUnableToFindASystematicStudy {
        @Test
        fun `should prepare a fail view when trying to find a nonexistent systematic study`() {
            val (researcher, systematicStudy) = factory

            preconditionCheckerMocking.makeSystematicStudyNonexistent()

            sut.findById(presenter, researcher, systematicStudy)
            verify {
                presenter.prepareFailView(any<EntityNotFoundException>())
                presenter.isDone()
            }
        }

        @Test
        fun `should prepare a fail view if the researcher is not a collaborator`() {
            val (researcher, systematicStudy) = factory

            preconditionCheckerMocking.makeResearcherNotACollaborator()

            sut.findById(presenter, researcher, systematicStudy)
            verify {
                presenter.prepareFailView(any<UnauthorizedUserException>())
                presenter.isDone()
            }
        }

        @Test
        fun `should a unauthenticated researcher be unable to find any systematic study`() {
            val (researcher, systematicStudy) = factory

            preconditionCheckerMocking.makeResearcherUnauthenticated()

            sut.findById(presenter, researcher, systematicStudy)
            verify {
                presenter.isDone()
                presenter.prepareFailView(any<UnauthenticatedUserException>())
            }
        }

        @Test
        fun `should a unauthorized researcher be unable to find any systematic study`() {
            val (researcher, systematicStudy) = factory

            preconditionCheckerMocking.makeResearcherUnauthorized()

            sut.findById(presenter, researcher, systematicStudy)
            verify {
                presenter.isDone()
                presenter.prepareFailView(any<UnauthorizedUserException>())
            }
        }
    }
}
