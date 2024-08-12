package br.all.application.review.find.services

import br.all.application.review.find.presenter.FindAllSystematicStudyPresenter
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.review.util.TestDataFactory
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
class FindAllSystematicStudiesServiceImplTest {
    @MockK
    private lateinit var repository: SystematicStudyRepository
    @MockK
    private lateinit var credentialsService: CredentialsService
    @MockK(relaxed = true)
    private lateinit var presenter: FindAllSystematicStudyPresenter
    @InjectMockKs
    private lateinit var sut: FindAllSystematicStudiesServiceImpl

    private lateinit var factory: TestDataFactory
    private lateinit var preconditionCheckerMocking: PreconditionCheckerMockingNew

    @BeforeEach
    fun setUp() = run {
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
    @DisplayName("When successfully finding systematic studies")
    inner class WhenSuccessfullyFindingSystematicStudies {
        @BeforeEach
        fun setUp() = run { preconditionCheckerMocking.makeEverythingWork() }

        @Test
        fun `should find the only existent systematic study`() {
            val (researcher) = factory
            val response = factory.findAllResponseModel(1)

            every { repository.findAllByCollaborator(researcher) } returns response.systematicStudies

            sut.findAllByCollaborator(presenter, researcher)
            verify(exactly = 1) { presenter.prepareSuccessView(response) }
        }

        @Test
        fun `should find all the several systematic studies`() {
            val (researcher) = factory
            val response = factory.findAllResponseModel(3)

            every { repository.findAllByCollaborator(researcher) } returns response.systematicStudies

            sut.findAllByCollaborator(presenter, researcher)
            verify { presenter.prepareSuccessView(response) }
        }

        @Test
        fun `should find all the systematic studies of a owner`() {
            val (researcher, _, owner) = factory
            val request = factory.findByOwnerRequest()
            val response = factory.findAllByOwnerResponseModel(1)

            every { repository.findAllByCollaboratorAndOwner(researcher, owner) } returns response.systematicStudies

            sut.findAllByOwner(presenter, request)
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

            sut.findAllByCollaborator(presenter, researcher)
            verify { presenter.prepareSuccessView(response) }
        }

        @Test
        fun `should not find any systematic study when a owner has no one`() {
            val (researcher, _, owner) = factory
            val request = factory.findByOwnerRequest()
            val response = factory.emptyFindAllResponseModel(owner = owner)

            preconditionCheckerMocking.makeEverythingWork()
            every { repository.findAllByCollaboratorAndOwner(researcher, owner) } returns emptyList()

            sut.findAllByOwner(presenter, request)
            verify { presenter.prepareSuccessView(response) }
        }

        @Test
        fun `should prepare fail view when the researcher is unauthenticated`() {
            val (researcher) = factory

            preconditionCheckerMocking.makeUserUnauthenticated()

            sut.findAllByCollaborator(presenter, researcher)
            verifyOrder {
                presenter.prepareFailView(any<UnauthenticatedUserException>())
                presenter.isDone()
            }
        }

        @Test
        fun `should prepare fail view when the researcher is unauthorized`() {
            val (researcher) = factory

            preconditionCheckerMocking.makeUserUnauthorized()

            sut.findAllByCollaborator(presenter, researcher)
            verifyOrder {
                presenter.prepareFailView(any<UnauthorizedUserException>())
                presenter.isDone()
            }
        }
    }
}
