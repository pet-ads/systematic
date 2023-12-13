package br.all.application.review.create

import br.all.application.researcher.credentials.ResearcherCredentialsService
import br.all.application.review.create.CreateSystematicStudyService.RequestModel
import br.all.application.review.create.CreateSystematicStudyService.ResponseModel
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.review.repository.fromRequestModel
import br.all.application.review.repository.toDto
import br.all.application.shared.exceptions.UnauthenticatedUserException
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
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

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
    private lateinit var createSystematicStudyPresenter: FakeCreateSystematicStudyPresenter
    private lateinit var sut: CreateSystematicStudyServiceImpl

    @BeforeEach
    fun setUp() {
        createSystematicStudyPresenter = FakeCreateSystematicStudyPresenter()
        sut = CreateSystematicStudyServiceImpl(systematicStudyRepository, uuidGeneratorService, credentialsService)
    }

    @Nested
    @Tag("ValidClasses")
    @DisplayName("When successfully creating a Systematic Study")
    inner class WhenSuccessfullyCreatingASystematicStudy {
        @Test
        fun `Should successfully create a systematic study`() {
            val researcherUuid = UUID.randomUUID()
            val researcherId = ResearcherId(researcherUuid)
            val systematicStudyId = UUID.randomUUID()
            val request = RequestModel("Title", "Description", emptySet())
            val dto = SystematicStudy.fromRequestModel(systematicStudyId, researcherUuid, request).toDto()
            val response = ResponseModel(researcherUuid, systematicStudyId)

            every { credentialsService.isAuthenticated(researcherId) } returns true
            every { credentialsService.hasAuthority(researcherId) } returns true
            every { uuidGeneratorService.next() } returns systematicStudyId
            every { systematicStudyRepository.saveOrUpdate(dto) } just Runs

            sut.create(createSystematicStudyPresenter, researcherUuid, request)

            verify {
                credentialsService.isAuthenticated(researcherId)
                credentialsService.hasAuthority(researcherId)
                uuidGeneratorService.next()
                systematicStudyRepository.saveOrUpdate(dto)
            }

            assertAll(
                { assertEquals(response, createSystematicStudyPresenter.responseModel) },
                { assertNull(createSystematicStudyPresenter.throwable) },
            )
        }
    }

    @Nested
    @DisplayName("When unable to create a new Systematic Study")
    inner class WhenUnableToCreateANewSystematicStudy {
        @Test
        fun `Should not the researcher be allowed to create a new study when unauthenticated`() {
            val researcherId = ResearcherId(UUID.randomUUID())
            val request = RequestModel("Title", "Description", emptySet())

            every { credentialsService.isAuthenticated(researcherId) } returns false
            every { credentialsService.hasAuthority(researcherId) } returns true

            sut.create(createSystematicStudyPresenter, researcherId.value, request)

            verify { credentialsService.isAuthenticated(researcherId) }
            verify(exactly = 0) { credentialsService.hasAuthority(researcherId) }

            assertAll(
                { assertNull(createSystematicStudyPresenter.responseModel) },
                { assertTrue { createSystematicStudyPresenter.throwable is UnauthenticatedUserException } }
            )
        }
    }
}
