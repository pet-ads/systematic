package br.all.application.search.find

import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.search.find.presenter.FindAllSearchSessionsPresenter
import br.all.application.search.find.service.FindAllSearchSessionsServiceImpl
import br.all.application.search.repository.SearchSessionRepository
import br.all.application.search.util.TestDataFactory
import br.all.domain.shared.exception.UnauthenticatedUserException
import br.all.domain.shared.exception.UnauthorizedUserException
import br.all.application.user.CredentialsService
import br.all.application.util.PreconditionCheckerMockingNew
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@Tag("UnitTest")
@Tag("ServiceTest")
@ExtendWith(MockKExtension::class)
class FindAllSearchSessionsServiceImplTest {

    @MockK(relaxUnitFun = true)
    private lateinit var systematicStudyRepository: SystematicStudyRepository

    @MockK(relaxUnitFun = true)
    private lateinit var searchSessionRepository: SearchSessionRepository

    @MockK
    private lateinit var credentialService: CredentialsService

    @MockK(relaxed = true)
    private lateinit var presenter: FindAllSearchSessionsPresenter

    private lateinit var sut: FindAllSearchSessionsServiceImpl
    private lateinit var factory: TestDataFactory
    private lateinit var preconditionCheckerMocking: PreconditionCheckerMockingNew

    @BeforeEach
    fun setUp() = run {
        factory = TestDataFactory()
        preconditionCheckerMocking = PreconditionCheckerMockingNew(
            presenter,
            credentialService,
            systematicStudyRepository,
            factory.userId,
            factory.systematicStudyId
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
                credentialService
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
                credentialService
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
            preconditionCheckerMocking.makeUserUnauthenticated()

            sut.findAllSearchSessions(presenter, factory.findAllRequestModel())
            verify {
                presenter.prepareFailView(any<UnauthenticatedUserException>())
            }
        }

        @Test
        fun `should prepare fail view when the researcher is unauthorized`() {

            preconditionCheckerMocking.makeUserUnauthorized()

            sut.findAllSearchSessions(presenter, factory.findAllRequestModel())
            verify {
                presenter.prepareFailView(any<UnauthorizedUserException>())
            }
        }
    }
}