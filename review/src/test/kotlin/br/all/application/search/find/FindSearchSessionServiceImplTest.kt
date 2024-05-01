package br.all.application.search.find

import br.all.application.user.credentials.ResearcherCredentialsService
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.search.find.presenter.FindSearchSessionPresenter
import br.all.application.search.find.service.FindSearchSessionServiceImpl
import br.all.application.search.repository.SearchSessionRepository
import br.all.application.search.util.TestDataFactory
import br.all.application.shared.exceptions.EntityNotFoundException
import br.all.application.shared.exceptions.UnauthenticatedUserException
import br.all.application.shared.exceptions.UnauthorizedUserException
import br.all.application.util.PreconditionCheckerMocking
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class FindSearchSessionServiceImplTest {
    @MockK(relaxUnitFun = true)
    private lateinit var systematicStudyRepository: SystematicStudyRepository

    @MockK(relaxUnitFun = true)
    private lateinit var searchSessionRepository: SearchSessionRepository

    @MockK
    private lateinit var credentialsService: ResearcherCredentialsService

    @MockK(relaxed = true)
    private lateinit var presenter: FindSearchSessionPresenter

    private lateinit var sut: FindSearchSessionServiceImpl
    private lateinit var factory: TestDataFactory
    private lateinit var preconditionCheckerMocking: PreconditionCheckerMocking

    @BeforeEach
    fun setUp() = run {
        factory = TestDataFactory()
        preconditionCheckerMocking = PreconditionCheckerMocking(
            presenter,
            credentialsService,
            systematicStudyRepository,
            factory.researcherId,
            factory.systematicStudyId,
        )
    }

    @Nested
    @DisplayName("When successfully finding search session")
    inner class WhenSuccessfullyFindingSearchSession {
        @BeforeEach
        fun setUp() {
            sut = FindSearchSessionServiceImpl(
                systematicStudyRepository,
                searchSessionRepository,
                credentialsService
            )
            run { preconditionCheckerMocking.makeEverythingWork() }
        }

        @Test
        fun `should correctly find a search session and prepare a success view`() {
            val response = factory.findOneResponseModel()

            preconditionCheckerMocking.makeEverythingWork()
            every { searchSessionRepository.findById(factory.searchSessionId) } returns response.content

            sut.findOneSession(presenter, factory.findOneRequestModel())
            verify { presenter.prepareSuccessView(response) }
        }
    }

    @Nested
    @DisplayName("When fail finding search session")
    inner class WhenFailFindingSearchSession {
        @BeforeEach
        fun setUp() {
            sut = FindSearchSessionServiceImpl(
                systematicStudyRepository,
                searchSessionRepository,
                credentialsService
            )
        }

        @Test
        fun `should prepare a fail view when trying to find a nonexistent search session`() {

            preconditionCheckerMocking.makeEverythingWork()

            every { searchSessionRepository.findById(any()) } returns null

            sut.findOneSession(presenter, factory.findOneRequestModel())

            verify {
                presenter.prepareFailView(match { it is EntityNotFoundException })
                presenter.isDone()
            }
        }

        @Test
        fun `should prepare fail view when the researcher is unauthenticated`() {
            preconditionCheckerMocking.makeResearcherUnauthenticated()

            sut.findOneSession(presenter, factory.findOneRequestModel())
            verify {
                presenter.prepareFailView(any<UnauthenticatedUserException>())
            }
        }

        @Test
        fun `should prepare fail view when the researcher is unauthorized`() {

            preconditionCheckerMocking.makeResearcherUnauthorized()

            sut.findOneSession(presenter, factory.findOneRequestModel())
            verify {
                presenter.prepareFailView(any<UnauthorizedUserException>())
            }
        }
    }
}