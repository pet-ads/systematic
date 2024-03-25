package br.all.application.search.update

import br.all.application.researcher.credentials.ResearcherCredentialsService
import br.all.application.review.repository.SystematicStudyDto
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.search.repository.SearchSessionDto
import br.all.application.search.repository.SearchSessionRepository
import br.all.application.search.util.TestDataFactory
import br.all.application.shared.exceptions.EntityNotFoundException
import br.all.application.shared.exceptions.UnauthenticatedUserException
import br.all.application.shared.exceptions.UnauthorizedUserException
import br.all.application.util.PreconditionCheckerMocking
import io.github.serpro69.kfaker.provider.Prince
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
class UpdateSearchSessionServiceImplTest {

    @MockK(relaxUnitFun = true)
    private lateinit var systematicStudyRepository: SystematicStudyRepository

    @MockK(relaxUnitFun = true)
    private lateinit var searchSessionRepository: SearchSessionRepository

    @MockK
    private lateinit var credentialsService: ResearcherCredentialsService

    @MockK(relaxed = true)
    private lateinit var presenter: UpdateSearchSessionPresenter
    @InjectMockKs
    private lateinit var sut: UpdateSearchSessionServiceImpl

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
    @DisplayName("When successfully updating search session")
    inner class WhenSuccessfullyUpdatingSearchSession {
        @BeforeEach
        fun setUp() = run { preconditionCheckerMocking.makeEverythingWork() }

        @Test
        fun `should only the search string be updated`() {
            val dto = factory.generateDto(id = factory.searchSessionId, searchString = "Old searchString")
            val updatedDto = dto.copy(searchString = "New searchString")
            val request = factory.updateRequestModel(searchString = "New searchString")
            val response = factory.updateResponseModel()

            every { searchSessionRepository.existsById(factory.searchSessionId) } returns true
            every { searchSessionRepository.findById(factory.searchSessionId) } returns dto
            sut.updateSession(presenter, request)

            verify {
                searchSessionRepository.saveOrUpdate(updatedDto)
                presenter.prepareSuccessView(response)
            }
        }

        @Test
        fun `should only the additional info be updated`() {
            val dto = factory.generateDto(id = factory.searchSessionId, additionalInfo = "Old additionalInfo")
            val updatedDto = dto.copy(additionalInfo = "New additionalInfo")
            val request = factory.updateRequestModel(additionalInfo = "New additionalInfo")
            val response = factory.updateResponseModel()

            every { searchSessionRepository.existsById(factory.searchSessionId) } returns true
            every { searchSessionRepository.findById(factory.searchSessionId) } returns dto
            sut.updateSession(presenter, request)

            verify {
                searchSessionRepository.saveOrUpdate(updatedDto)
                presenter.prepareSuccessView(response)
            }
        }

        @Test
        fun `should only the search source be updated`() {
            val dto = factory.generateDto(id = factory.searchSessionId, source = "Old Source")
            val updatedDto = dto.copy(source = "New Source")
            val request = factory.updateRequestModel(source = "New Source")
            val response = factory.updateResponseModel()

            every { searchSessionRepository.existsById(factory.searchSessionId) } returns true
            every { searchSessionRepository.findById(factory.searchSessionId) } returns dto
            sut.updateSession(presenter, request)

            verify {
                searchSessionRepository.saveOrUpdate(updatedDto)
                presenter.prepareSuccessView(response)
            }
        }

        @Test
        fun `Should searchString, additionalInfo, and source all be updated`() {
            val dto = factory.generateDto(
                searchString = "Old searchString",
                additionalInfo = "Old additionalInfo",
                source = "Old Source"
            )
            val updatedDto = dto.copy(
                searchString = "New searchString",
                additionalInfo = "New additionalInfo",
                source = "New Source"
            )
            val request = factory.updateRequestModel(
                searchString = "New searchString",
                additionalInfo = "New additionalInfo",
                source = "New Source"
            )
            val response = factory.updateResponseModel()
            every { searchSessionRepository.existsById(factory.searchSessionId) } returns true
            every { searchSessionRepository.findById(factory.searchSessionId) } returns dto
            sut.updateSession(presenter, request)

            verify {
                searchSessionRepository.saveOrUpdate(updatedDto)
                presenter.prepareSuccessView(response)
            }
        }
    }
    @Nested
    @DisplayName("When the being unable to perform updates")
    inner class WhenTheBeingUnableToPerformUpdates {
        @Test
        fun `should nothing happen when both searchString and additionalInfo are not given`() {
            val dto = factory.generateDto()
            val request = factory.updateRequestModel()
            val response = factory.updateResponseModel()

            preconditionCheckerMocking.makeEverythingWork()
            every { searchSessionRepository.existsById(factory.searchSessionId) } returns true
            every { searchSessionRepository.findById(factory.searchSessionId) } returns dto

            sut.updateSession(presenter, request)

            verify(exactly = 0) { searchSessionRepository.saveOrUpdate(any<SearchSessionDto>()) }
            verify { presenter.prepareSuccessView(response) }
        }

        @Test
        fun `should prepare fail view with EntityNotFoundException when the search session does not exist`() {
            val request = factory.updateRequestModel()

            preconditionCheckerMocking.makeEverythingWork()

            every { searchSessionRepository.existsById(factory.searchSessionId) } returns false

            sut.updateSession(presenter, request)

            verify {
                searchSessionRepository.existsById(factory.searchSessionId)
                presenter.prepareFailView(any<EntityNotFoundException>())
                presenter.isDone()
            }
        }
        @Test
        fun `should the researcher be unauthorized if they are not a collaborator`() {
            val request = factory.updateRequestModel()

            preconditionCheckerMocking.makeResearcherNotACollaborator()
            sut.updateSession(presenter, request)

            verifyOrder {
                presenter.prepareFailView(any<UnauthorizedUserException>())
                presenter.isDone()
            }
        }

        @Test
        fun `should prepare fail view if the researcher is unauthenticated`() {
            val request = factory.updateRequestModel()

            preconditionCheckerMocking.makeResearcherUnauthenticated()
            sut.updateSession(presenter, request)

            verifyOrder {
                presenter.prepareFailView(any<UnauthenticatedUserException>())
                presenter.isDone()
            }
        }

        @Test
        fun `should prepare fail view if the researcher is unauthorized`() {
            val request = factory.updateRequestModel()

            preconditionCheckerMocking.makeResearcherUnauthorized()
            sut.updateSession(presenter, request)

            verifyOrder {
                presenter.prepareFailView(any<UnauthorizedUserException>())
                presenter.isDone()
            }
        }
    }
}
