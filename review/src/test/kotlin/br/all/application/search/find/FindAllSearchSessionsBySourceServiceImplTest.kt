package br.all.application.search.find

import br.all.application.researcher.credentials.ResearcherCredentialsService
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.search.find.presenter.FindAllSearchSessionsBySourcePresenter
import br.all.application.search.find.service.FindAllSearchSessionsBySourceServiceImpl
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
class FindAllSearchSessionsBySourceServiceImplTest {

    @MockK(relaxUnitFun = true)
    private lateinit var systematicStudyRepository: SystematicStudyRepository

    @MockK(relaxUnitFun = true)
    private lateinit var searchSessionRepository: SearchSessionRepository

    @MockK
    private lateinit var credentialsService: ResearcherCredentialsService

    @MockK(relaxed = true)
    private lateinit var presenter: FindAllSearchSessionsBySourcePresenter

    private lateinit var sut: FindAllSearchSessionsBySourceServiceImpl
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
            sut = FindAllSearchSessionsBySourceServiceImpl(
                systematicStudyRepository,
                searchSessionRepository,
                credentialsService
            )
            run { preconditionCheckerMocking.makeEverythingWork() }
        }
        @Test
        fun `should find the only one existent search session with the given source`(){
            val response = factory.findAllBySourceResponseModel(1)
            every {
                searchSessionRepository.findSearchSessionsBySource(factory.systematicStudyId, factory.source)
            } returns response.searchSessions
            sut.findAllSessionsBySource(presenter, factory.findAllBySourceRequestModel())
            verify(exactly = 1) { presenter.prepareSuccessView(response) }
        }
        @Test
        fun `should find multiple search sessions`(){
            val response = factory.findAllBySourceResponseModel(5)
            every { searchSessionRepository.findSearchSessionsBySource(
                factory.systematicStudyId, factory.source)} returns response.searchSessions
            sut.findAllSessionsBySource(presenter, factory.findAllBySourceRequestModel())
            verify{ presenter.prepareSuccessView(response) }
        }
    }

    @Nested
    @DisplayName("When fail finding a search session")
    inner class WhenFailFindingSearchSession {
        @BeforeEach
        fun setUp() {
            sut = FindAllSearchSessionsBySourceServiceImpl(
                systematicStudyRepository,
                searchSessionRepository,
                credentialsService
            )
        }
        @Test
        fun `should not find a search session with the given source when no one exists`(){
            val response = factory.emptyFindBySourceResponseModel()

            preconditionCheckerMocking.makeEverythingWork()

            every {
                searchSessionRepository.findSearchSessionsBySource(factory.systematicStudyId, factory.source)
            } returns emptyList()

            sut.findAllSessionsBySource(presenter, factory.findAllBySourceRequestModel())
            verify { presenter.prepareSuccessView(response) }
        }
        @Test
        fun `should prepare fail view when the researcher is unauthenticated`() {
            preconditionCheckerMocking.makeResearcherUnauthenticated()

            sut.findAllSessionsBySource(presenter, factory.findAllBySourceRequestModel())
            verify {
                presenter.prepareFailView(any<UnauthenticatedUserException>())
            }
        }

        @Test
        fun `should prepare fail view when the researcher is unauthorized`() {

            preconditionCheckerMocking.makeResearcherUnauthorized()

            sut.findAllSessionsBySource(presenter, factory.findAllBySourceRequestModel())
            verify {
                presenter.prepareFailView(any<UnauthorizedUserException>())
            }
        }
    }
}