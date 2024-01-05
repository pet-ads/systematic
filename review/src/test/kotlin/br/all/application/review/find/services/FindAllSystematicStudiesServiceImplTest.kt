package br.all.application.review.find.services

import br.all.application.researcher.credentials.ResearcherCredentialsService
import br.all.application.review.find.presenter.FindAllSystematicStudyPresenter
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.review.util.CredentialsServiceMockBuilder.makeResearcherToBeAllowed
import br.all.application.review.util.CredentialsServiceMockBuilder.makeResearcherToBeUnauthenticated
import br.all.application.review.util.CredentialsServiceMockBuilder.makeResearcherToBeUnauthorized
import br.all.application.review.util.TestDataFactory
import br.all.application.shared.exceptions.UnauthenticatedUserException
import br.all.application.shared.exceptions.UnauthorizedUserException
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import java.util.*

@Tag("UnitTest")
@Tag("IntegrationTest")
@ExtendWith(MockKExtension::class)
class FindAllSystematicStudiesServiceImplTest {
    @MockK
    private lateinit var systematicStudyRepository: SystematicStudyRepository
    @MockK
    private lateinit var credentialsService: ResearcherCredentialsService
    private lateinit var presenter: FindAllSystematicStudyPresenter
    private lateinit var factory: TestDataFactory
    private lateinit var sut: FindAllSystematicStudiesServiceImpl

    @BeforeEach
    fun setUp() {
        presenter = mockk(relaxed = true)
        factory = TestDataFactory()
        sut = FindAllSystematicStudiesServiceImpl(systematicStudyRepository, credentialsService)
    }

    @Nested
    @Tag("ValidClasses")
    @DisplayName("When successfully finding systematic studies")
    inner class WhenSuccessfullyFindingSystematicStudies {
        @Test
        fun `should find the only existent systematic study`() {
            val response = factory.findAllResponseModel(factory.generateDto())

            makeResearcherToBeAllowed(credentialsService, presenter, factory.researcherId)
            every {
                systematicStudyRepository.findSomeByCollaborator(factory.researcherId)
            } returns response.systematicStudies

            sut.findAll(presenter, factory.researcherId)
            verify { presenter.prepareSuccessView(response) }
        }

        @Test
        fun `should find all the several systematic studies`() {
            val response = factory.findAllResponseModel(
                factory.generateDto(systematicStudyId = UUID.randomUUID()),
                factory.generateDto(systematicStudyId = UUID.randomUUID()),
                factory.generateDto(systematicStudyId = UUID.randomUUID()),
            )

            makeResearcherToBeAllowed(credentialsService, presenter, factory.researcherId)
            every {
                systematicStudyRepository.findSomeByCollaborator(factory.researcherId)
            } returns response.systematicStudies

            sut.findAll(presenter, factory.researcherId)
            verify { presenter.prepareSuccessView(response) }
        }
    }

    @Nested
    @DisplayName("When being unable to find systematic studies")
    inner class WhenBeingUnableToFindSystematicStudies {
        @Test
        fun `should not find systematic studies when no one exists`() {
            val response = factory.emptyFindAllResponseModel()

            makeResearcherToBeAllowed(credentialsService, presenter, factory.researcherId)
            every { systematicStudyRepository.findSomeByCollaborator(factory.researcherId) } returns emptyList()

            sut.findAll(presenter, factory.researcherId)
            verify { presenter.prepareSuccessView(response) }
        }

        @Test
        fun `should prepare fail view when the researcher is unauthenticated`() {
            makeResearcherToBeUnauthenticated(credentialsService, presenter, factory.researcherId)
            sut.findAll(presenter, factory.researcherId)
            verify {
                presenter.isDone()
                presenter.prepareFailView(any<UnauthenticatedUserException>())
            }
        }

        @Test
        fun `should prepare fail view when the researcher is unauthorized`() {
            makeResearcherToBeUnauthorized(credentialsService, presenter, factory.researcherId)
            sut.findAll(presenter, factory.researcherId)
            verify {
                presenter.isDone()
                presenter.prepareFailView(any<UnauthorizedUserException>())
            }
        }
    }
}
