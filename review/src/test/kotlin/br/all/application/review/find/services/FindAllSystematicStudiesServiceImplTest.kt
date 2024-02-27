package br.all.application.review.find.services

import br.all.application.researcher.credentials.ResearcherCredentialsService
import br.all.application.review.find.presenter.FindAllSystematicStudyPresenter
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.review.util.TestDataFactory
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
class FindAllSystematicStudiesServiceImplTest {
    @MockK
    private lateinit var repository: SystematicStudyRepository
    @MockK
    private lateinit var credentialsService: ResearcherCredentialsService
    @MockK(relaxed = true)
    private lateinit var presenter: FindAllSystematicStudyPresenter
    @InjectMockKs
    private lateinit var sut: FindAllSystematicStudiesServiceImpl

    private lateinit var factory: TestDataFactory
    private lateinit var preconditionCheckerMocking: PreconditionCheckerMocking

    @BeforeEach
    fun setUp() = run {
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
    @DisplayName("When successfully finding systematic studies")
    inner class WhenSuccessfullyFindingSystematicStudies {
        @BeforeEach
        fun setUp() = run { preconditionCheckerMocking.makeEverythingWork() }

        @Test
        fun `should find the only existent systematic study`() {
            val (researcher) = factory
            val response = factory.findAllResponseModel(1)

            every { repository.findAllByCollaborator(researcher) } returns response.systematicStudies

            sut.findAll(presenter, researcher)
            verify(exactly = 1) { presenter.prepareSuccessView(response) }
        }

        @Test
        fun `should find all the several systematic studies`() {
            val (researcher) = factory
            val response = factory.findAllResponseModel(3)

            every { repository.findAllByCollaborator(researcher) } returns response.systematicStudies

            sut.findAll(presenter, researcher)
            verify { presenter.prepareSuccessView(response) }
        }

        @Test
        fun `should find all the systematic studies of a owner`() {
            val (researcher, _, owner) = factory
            val response = factory.findAllByOwnerResponseModel(1)

            every { repository.findAllByCollaboratorAndOwner(researcher, owner) } returns response.systematicStudies

            sut.findAllByOwner(presenter, researcher, owner)
            verify { presenter.prepareSuccessView(response) }
        }
    }

    @Nested
    @Tag("InvalidClasses")
    @DisplayName("When being unable to find systematic studies")
    inner class WhenBeingUnableToFindSystematicStudies {
        @Test
        fun `should not find systematic studies when no one exists`() {
            val (researcher) = factory
            val response = factory.emptyFindAllResponseModel()

            preconditionCheckerMocking.makeEverythingWork()
            every { repository.findAllByCollaborator(researcher) } returns emptyList()

            sut.findAll(presenter, researcher)
            verify { presenter.prepareSuccessView(response) }
        }

        @Test
        fun `should not find any systematic study when a owner has no one`() {
            val (researcher, owner) = factory
            val response = factory.emptyFindAllResponseModel(owner = owner)

            preconditionCheckerMocking.makeEverythingWork()
            every { repository.findAllByCollaboratorAndOwner(researcher, owner) } returns emptyList()

            sut.findAllByOwner(presenter, researcher, owner)
            verify { presenter.prepareSuccessView(response) }
        }

        @Test
        fun `should prepare fail view when the researcher is unauthenticated`() {
            val (researcher) = factory

            preconditionCheckerMocking.makeResearcherUnauthenticated()

            sut.findAll(presenter, researcher)
            verify {
                presenter.isDone()
                presenter.prepareFailView(any<UnauthenticatedUserException>())
            }
        }

        @Test
        fun `should prepare fail view when the researcher is unauthorized`() {
            val (researcher) = factory

            preconditionCheckerMocking.makeResearcherUnauthorized()

            sut.findAll(presenter, researcher)
            verify {
                presenter.isDone()
                presenter.prepareFailView(any<UnauthorizedUserException>())
            }
        }
    }
}
