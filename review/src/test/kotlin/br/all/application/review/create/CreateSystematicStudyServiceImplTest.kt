package br.all.application.review.create

import br.all.application.researcher.credentials.ResearcherCredentialsService
import br.all.application.review.create.CreateSystematicStudyService.ResponseModel
import br.all.application.review.repository.SystematicStudyDto
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.review.util.CredentialsServiceMockBuilder.makeResearcherToBeAllowed
import br.all.application.review.util.CredentialsServiceMockBuilder.makeResearcherToBeUnauthenticated
import br.all.application.review.util.CredentialsServiceMockBuilder.makeResearcherToBeUnauthorized
import br.all.application.review.util.TestDataFactory
import br.all.application.shared.exceptions.UnauthenticatedUserException
import br.all.application.shared.exceptions.UnauthorizedUserException
import br.all.domain.model.researcher.ResearcherId
import br.all.domain.services.UuidGeneratorService
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.verify
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import java.util.*

@Tag("UnitTest")
@Tag("ServiceTest")
@ExtendWith(MockKExtension::class)
class CreateSystematicStudyServiceImplTest {
    @MockK
    private lateinit var repository: SystematicStudyRepository
    @MockK
    private lateinit var uuidGeneratorService: UuidGeneratorService
    @MockK
    private lateinit var credentialsService: ResearcherCredentialsService
    @MockK
    private lateinit var presenter: CreateSystematicStudyPresenter
    private lateinit var factory: TestDataFactory
    private lateinit var sut: CreateSystematicStudyServiceImpl

    @BeforeEach
    fun setUp() {
        factory = TestDataFactory()
        sut = CreateSystematicStudyServiceImpl(repository, uuidGeneratorService, credentialsService)
    }

    @Nested
    @Tag("ValidClasses")
    @DisplayName("When successfully creating a Systematic Study")
    inner class WhenSuccessfullyCreatingASystematicStudy {
        @Test
        fun `should successfully create a systematic study`() {
            val researcherId = ResearcherId(UUID.randomUUID())
            val systematicStudyId = UUID.randomUUID()
            val request = factory.createRequestModel()
            val dto = factory.createDtoFromCreateRequestModel(systematicStudyId, researcherId, request)
            val response = factory.createResponseModel(researcherId, systematicStudyId)

            makeResearcherToBeAllowed(credentialsService, presenter, researcherId.value)
            mockkSystematicStudyToBeCreated(systematicStudyId, dto, response)

            sut.create(presenter, researcherId.value, request)

            verify(exactly = 1) {
                uuidGeneratorService.next()
                repository.saveOrUpdate(dto)
                presenter.prepareSuccessView(response)
            }
        }

        private fun mockkSystematicStudyToBeCreated(
            systematicStudyId: UUID,
            dto: SystematicStudyDto,
            response: ResponseModel
        ) {
            every { uuidGeneratorService.next() } returns systematicStudyId
            every { repository.saveOrUpdate(dto) } just Runs
            every { presenter.prepareSuccessView(response) } just Runs
        }
    }

    @Nested
    @Tag("InvalidClasses")
    @DisplayName("When unable to create a new Systematic Study")
    inner class WhenUnableToCreateANewSystematicStudy {
        @Test
        fun `should not the researcher be allowed to create a new study when unauthenticated`() {
            val researcherId = ResearcherId(UUID.randomUUID())
            val request = factory.createRequestModel()

            makeResearcherToBeUnauthenticated(credentialsService, presenter, researcherId.value)
            sut.create(presenter, researcherId.value, request)

            verify(exactly = 1) {
                presenter.prepareFailView(any<UnauthenticatedUserException>())
                presenter.isDone()
            }
        }

        @Test
        fun `should not the researcher be allowed to create a study when unauthorized`() {
            val researcherId = ResearcherId(UUID.randomUUID())
            val request = factory.createRequestModel()

            makeResearcherToBeUnauthorized(credentialsService, presenter, researcherId.value)
            sut.create(presenter, researcherId.value, request)

            verify(exactly = 1) {
                presenter.prepareFailView(any<UnauthorizedUserException>())
                presenter.isDone()
            }
        }
    }
}
