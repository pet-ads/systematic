package br.all.application.search.find

import br.all.application.researcher.credentials.ResearcherCredentialsService
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.search.CreateSearchSessionServiceImpl
import br.all.application.search.create.CreateSearchSessionPresenter
import br.all.application.search.find.presenter.FindAllSearchSessionsPresenter
import br.all.application.search.find.service.FindAllSearchSessionsServiceImpl
import br.all.application.search.repository.SearchSessionRepository
import br.all.application.search.util.TestDataFactory
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
class FindAllSearchSessionsServiceImplTest {

    @MockK(relaxUnitFun = true)
    private lateinit var systematicStudyRepository: SystematicStudyRepository

    @MockK(relaxUnitFun = true)
    private lateinit var searchSessionRepository: SearchSessionRepository

    @MockK
    private lateinit var credentialsService: ResearcherCredentialsService

    @MockK(relaxed = true)
    private lateinit var presenter: FindAllSearchSessionsPresenter

    private lateinit var sut: FindAllSearchSessionsServiceImpl
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
            sut = FindAllSearchSessionsServiceImpl(
                systematicStudyRepository,
                searchSessionRepository,
                credentialsService
            )
            run { preconditionCheckerMocking.makeEverythingWork() }
        }

        @Test
        fun `should find the only one existent search session`(){
            val response = factory.findAllResponseModel(1)
            every { searchSessionRepository.findAllFromSystematicStudy(factory.systematicStudyId)} returns response.searchSessions
            sut.findAllSearchSessions(presenter, factory.findAllRequestModel())
            verify(exactly = 1) { presenter.prepareSuccessView(response) }
        }

        @Test
        fun `should find multiple search sessions`(){
            val response = factory.findAllResponseModel(5)
            every { searchSessionRepository.findAllFromSystematicStudy(factory.systematicStudyId)} returns response.searchSessions
            sut.findAllSearchSessions(presenter, factory.findAllRequestModel())
            verify{ presenter.prepareSuccessView(response) }
        }
    }

    @Nested
    @DisplayName("When fail finding a search session")
    inner class WhenFailFindingSearchSession {

        @BeforeEach
        fun setUp() {
            sut = FindAllSearchSessionsServiceImpl(
                systematicStudyRepository,
                searchSessionRepository,
                credentialsService
            )
        }
        @Test
        fun `should not find a search session when no one exists`(){
            val response = factory.emptyFindAllResponseModel()

            preconditionCheckerMocking.makeEverythingWork()

            every { searchSessionRepository.findAllFromSystematicStudy(factory.systematicStudyId)} returns emptyList()

            sut.findAllSearchSessions(presenter, factory.findAllRequestModel())
            verify { presenter.prepareSuccessView(response) }
        }

        @Test
        fun `should prepare fail view when the researcher is unauthenticated`() {
            preconditionCheckerMocking.makeResearcherUnauthenticated()

            sut.findAllSearchSessions(presenter, factory.findAllRequestModel())
            verify {
                presenter.prepareFailView(any<UnauthenticatedUserException>())
            }
        }

        @Test
        fun `should prepare fail view when the researcher is unauthorized`() {

            preconditionCheckerMocking.makeResearcherUnauthorized()

            sut.findAllSearchSessions(presenter, factory.findAllRequestModel())
            verify {
                presenter.prepareFailView(any<UnauthorizedUserException>())
            }
        }
    }
}