@file:Suppress("ktlint:standard:no-wildcard-imports")

package br.all.application.search.delete

import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.search.repository.SearchSessionRepository
import br.all.application.search.util.TestDataFactory
import br.all.application.shared.exceptions.EntityNotFoundException
import br.all.application.shared.exceptions.UnauthenticatedUserException
import br.all.application.shared.exceptions.UnauthorizedUserException
import br.all.application.user.CredentialsService
import br.all.application.util.PreconditionCheckerMockingNew
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class DeleteSearchSessionServiceImplTest {
    @MockK(relaxUnitFun = true)
    private lateinit var systematicStudyRepository: SystematicStudyRepository

    @MockK(relaxUnitFun = true)
    private lateinit var searchSessionRepository: SearchSessionRepository

    @MockK(relaxed = true)
    private lateinit var presenter: DeleteSearchSessionPresenter

    @MockK
    private lateinit var credentialService: CredentialsService

    @InjectMockKs
    private lateinit var sut: DeleteSearchSessionServiceImpl

    private lateinit var factory: TestDataFactory
    private lateinit var preconditionCheckerMocking: PreconditionCheckerMockingNew

    @BeforeEach
    fun setUp() {
        factory = TestDataFactory()
        preconditionCheckerMocking =
            PreconditionCheckerMockingNew(
                presenter,
                credentialService,
                systematicStudyRepository,
                factory.userId,
                factory.systematicStudyId,
            )
    }

    @Nested
    @DisplayName("When successfully deleting search session")
    inner class WhenSuccessfullyDeletingSearchSession {
        @BeforeEach
        fun setUp() {
            preconditionCheckerMocking.makeEverythingWork()
        }

        @Test
        fun `should successfully delete search session`() {
            val request = factory.deleteRequestModel()
            val response = factory.deleteResponseModel()

            every { searchSessionRepository.findById(factory.searchSessionId) } returns factory.generateDto()
            every { searchSessionRepository.deleteById(factory.searchSessionId) } just Runs

            sut.delete(presenter, request)

            verify {
                searchSessionRepository.deleteById(factory.searchSessionId)
                presenter.prepareSuccessView(response)
            }
        }
    }

    @Nested
    @DisplayName("When unable to delete search session")
    inner class WhenUnableToDeleteSearchSession {
        @Test
        fun `should fail with EntityNotFoundException when session does not exist`() {
            val request = factory.deleteRequestModel()

            preconditionCheckerMocking.makeEverythingWork()
            every { searchSessionRepository.findById(factory.searchSessionId) } returns null

            sut.delete(presenter, request)

            verify {
                presenter.prepareFailView(any<EntityNotFoundException>())
                presenter.isDone()
            }
        }

        @Test
        fun `should the researcher be unauthorized if they are not a collaborator`() {
            val request = factory.deleteRequestModel()

            preconditionCheckerMocking.makeUserUnauthorized()
            sut.delete(presenter, request)

            verifyOrder {
                presenter.prepareFailView(any<UnauthorizedUserException>())
                presenter.isDone()
            }
        }

        @Test
        fun `should prepare fail view if the researcher is unauthenticated`() {
            val request = factory.deleteRequestModel()

            preconditionCheckerMocking.makeUserUnauthenticated()
            sut.delete(presenter, request)

            verifyOrder {
                presenter.prepareFailView(any<UnauthenticatedUserException>())
                presenter.isDone()
            }
        }

        @Test
        fun `should prepare fail view if the researcher is unauthorized`() {
            val request = factory.deleteRequestModel()

            preconditionCheckerMocking.makeUserUnauthorized()
            sut.delete(presenter, request)

            verifyOrder {
                presenter.prepareFailView(any<UnauthorizedUserException>())
                presenter.isDone()
            }
        }
    }
}
