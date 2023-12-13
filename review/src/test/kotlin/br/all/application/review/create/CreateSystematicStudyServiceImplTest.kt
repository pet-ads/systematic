package br.all.application.review.create

import br.all.application.researcher.credentials.FakeResearcherCredentialsService
import br.all.application.researcher.credentials.ResearcherCredentialsService
import br.all.application.review.create.CreateSystematicStudyService.RequestModel
import br.all.application.review.create.CreateSystematicStudyService.ResponseModel
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.review.repository.fromRequestModel
import br.all.application.review.repository.toDto
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

@Tag("UnitTest")
@Tag("ServiceTest")
@ExtendWith(MockKExtension::class)
class CreateSystematicStudyServiceImplTest {
    @MockK
    private lateinit var systematicStudyRepository: SystematicStudyRepository
    @MockK
    private lateinit var uuidGeneratorService: UuidGeneratorService
    private lateinit var createSystematicStudyPresenter: FakeCreateSystematicStudyPresenter
    private lateinit var credentialsService: ResearcherCredentialsService
    private lateinit var sut: CreateSystematicStudyServiceImpl

    @BeforeEach
    fun setUp() {
        credentialsService = FakeResearcherCredentialsService()
        createSystematicStudyPresenter = FakeCreateSystematicStudyPresenter()
        sut = CreateSystematicStudyServiceImpl(systematicStudyRepository, uuidGeneratorService, credentialsService)
    }

    @Nested
    @Tag("ValidClasses")
    @DisplayName("When successfully creating a Systematic Study")
    inner class WhenSuccessfullyCreatingASystematicStudy {
        @Test
        fun `Should successfully create a systematic study`() {
            val researcherId = UUID.randomUUID()
            val systematicStudyId = UUID.randomUUID()
            val request = RequestModel("Title", "Description", emptySet())
            val dto = SystematicStudy.fromRequestModel(systematicStudyId, researcherId, request).toDto()
            val response = ResponseModel(researcherId, systematicStudyId)

            every { uuidGeneratorService.next() } returns systematicStudyId
            every { systematicStudyRepository.saveOrUpdate(dto) } just Runs

            sut.create(createSystematicStudyPresenter, researcherId, request)

            verify {
                uuidGeneratorService.next()
                systematicStudyRepository.saveOrUpdate(dto)
            }

            assertAll(
                { assertEquals(response, createSystematicStudyPresenter.responseModel) },
                { assertNull(createSystematicStudyPresenter.throwable) },
            )
        }
    }
}
