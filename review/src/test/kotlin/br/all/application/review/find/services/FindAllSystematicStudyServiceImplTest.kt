package br.all.application.review.find.services

import br.all.application.researcher.credentials.ResearcherCredentialsService
import br.all.application.review.find.presenter.FindAllSystematicStudyPresenter
import br.all.application.review.find.services.FindAllSystematicStudyService.ResponseModel
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.review.util.CredentialsServiceMockBuilder.makeResearcherToBeAllowed
import br.all.application.review.util.TestDataFactory
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith

@Tag("UnitTest")
@Tag("IntegrationTest")
@ExtendWith(MockKExtension::class)
class FindAllSystematicStudyServiceImplTest {
    @MockK
    private lateinit var systematicStudyRepository: SystematicStudyRepository
    @MockK
    private lateinit var credentialsService: ResearcherCredentialsService
    private lateinit var presenter: FindAllSystematicStudyPresenter
    private lateinit var factory: TestDataFactory
    private lateinit var sut: FindAllSystematicStudyServiceImpl

    @BeforeEach
    fun setUp() {
        presenter = mockk(relaxed = true)
        factory = TestDataFactory()
        sut = FindAllSystematicStudyServiceImpl(systematicStudyRepository, credentialsService)
    }

    @Nested
    @Tag("ValidClasses")
    @DisplayName("When successfully finding systematic studies")
    inner class WhenSuccessfullyFindingSystematicStudies {
        @Test
        fun `should find the only existent systematic study`() {
            val response = ResponseModel(factory.researcherId, listOf(factory.generateDto()))

            makeResearcherToBeAllowed(credentialsService, presenter, factory.researcherId)
            every {
                systematicStudyRepository.findSomeByCollaborator(factory.researcherId)
            } returns response.systematicStudies

            sut.findAll(presenter, factory.researcherId)
            verify { presenter.prepareSuccessView(response) }
        }
    }
}
