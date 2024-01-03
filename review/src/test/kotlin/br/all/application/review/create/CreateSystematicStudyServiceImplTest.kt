package br.all.application.review.create

import br.all.application.researcher.credentials.ResearcherCredentialsService
import br.all.application.review.create.CreateSystematicStudyService.RequestModel
import br.all.application.review.create.CreateSystematicStudyService.ResponseModel
import br.all.application.review.repository.SystematicStudyDto
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.review.repository.fromRequestModel
import br.all.application.review.repository.toDto
import br.all.application.shared.exceptions.UnauthenticatedUserException
import br.all.application.shared.exceptions.UnauthorizedUserException
import br.all.domain.model.researcher.ResearcherId
import br.all.domain.model.review.SystematicStudy
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
    private lateinit var systematicStudyRepository: SystematicStudyRepository
    @MockK
    private lateinit var uuidGeneratorService: UuidGeneratorService
    @MockK
    private lateinit var credentialsService: ResearcherCredentialsService
    @MockK
    private lateinit var createSystematicStudyPresenter: CreateSystematicStudyPresenter
    private lateinit var sut: CreateSystematicStudyServiceImpl

    @BeforeEach
    fun setUp() {
        sut = CreateSystematicStudyServiceImpl(systematicStudyRepository, uuidGeneratorService, credentialsService)
    }

    @Nested
    @Tag("ValidClasses")
    @DisplayName("When successfully creating a Systematic Study")
    inner class WhenSuccessfullyCreatingASystematicStudy {
        @Test
        fun `Should successfully create a systematic study`() {
            val researcherId = ResearcherId(UUID.randomUUID())
            val systematicStudyId = UUID.randomUUID()
            val request = RequestModel("Title", "Description", emptySet())
            val dto = SystematicStudy.fromRequestModel(systematicStudyId, researcherId.value, request).toDto()
            val response = ResponseModel(researcherId.value, systematicStudyId)

            mockkResearcherToBeAllowed(researcherId)
            mockkSystematicStudyToBeCreated(systematicStudyId, dto, response)

            sut.create(createSystematicStudyPresenter, researcherId.value, request)

            verify(exactly = 1) {
                credentialsService.isAuthenticated(researcherId)
                credentialsService.hasAuthority(researcherId)
                uuidGeneratorService.next()
                systematicStudyRepository.saveOrUpdate(dto)
                createSystematicStudyPresenter.prepareSuccessView(response)
            }
        }

        private fun mockkResearcherToBeAllowed(researcherId: ResearcherId) {
            every { credentialsService.isAuthenticated(researcherId) } returns true
            every { credentialsService.hasAuthority(researcherId) } returns true
            every { createSystematicStudyPresenter.isDone() } returns false
        }

        private fun mockkSystematicStudyToBeCreated(
            systematicStudyId: UUID,
            dto: SystematicStudyDto,
            response: ResponseModel
        ) {
            every { uuidGeneratorService.next() } returns systematicStudyId
            every { systematicStudyRepository.saveOrUpdate(dto) } just Runs
            every { createSystematicStudyPresenter.prepareSuccessView(response) } just Runs
        }
    }

    @Nested
    @Tag("InvalidClasses")
    @DisplayName("When unable to create a new Systematic Study")
    inner class WhenUnableToCreateANewSystematicStudy {
        @Test
        fun `Should not the researcher be allowed to create a new study when unauthenticated`() {
            val researcherId = ResearcherId(UUID.randomUUID())
            val request = RequestModel("Title", "Description", emptySet())

            mockkResearcherToBeUnauthenticated(researcherId)
            sut.create(createSystematicStudyPresenter, researcherId.value, request)

            verify(exactly = 0) { credentialsService.hasAuthority(researcherId) }
            verify(exactly = 1) {
                credentialsService.isAuthenticated(researcherId)
                createSystematicStudyPresenter.prepareFailView(any<UnauthenticatedUserException>())
                createSystematicStudyPresenter.isDone()
            }
        }

        private fun mockkResearcherToBeUnauthenticated(researcherId: ResearcherId) {
            every { credentialsService.isAuthenticated(researcherId) } returns false
            every { credentialsService.hasAuthority(researcherId) } returns true
            every { createSystematicStudyPresenter.prepareFailView(any<UnauthenticatedUserException>()) } just Runs
            every { createSystematicStudyPresenter.isDone() } returns true
        }

        @Test
        fun `Should not the researcher be allowed to create a study when unauthorized`() {
            val researcherId = ResearcherId(UUID.randomUUID())
            val request = RequestModel("Title", "Description", emptySet())

            mockkResearcherToBeUnauthorized(researcherId)
            sut.create(createSystematicStudyPresenter, researcherId.value, request)

            verify(exactly = 1) {
                credentialsService.isAuthenticated(researcherId)
                credentialsService.hasAuthority(researcherId)
                createSystematicStudyPresenter.prepareFailView(any<UnauthorizedUserException>())
                createSystematicStudyPresenter.isDone()
            }
        }

        private fun mockkResearcherToBeUnauthorized(researcherId: ResearcherId) {
            every { credentialsService.isAuthenticated(researcherId) } returns true
            every { credentialsService.hasAuthority(researcherId) } returns false
            every { createSystematicStudyPresenter.prepareFailView(any<UnauthorizedUserException>()) } just Runs
            every { createSystematicStudyPresenter.isDone() } returns true
        }
    }
}
