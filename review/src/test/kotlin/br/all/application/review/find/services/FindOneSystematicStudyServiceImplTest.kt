package br.all.application.review.find.services

import br.all.application.researcher.credentials.ResearcherCredentialsService
import br.all.application.review.find.presenter.FindOneSystematicStudyPresenter
import br.all.application.review.find.services.FindOneSystematicStudyService.ResponseModel
import br.all.application.review.repository.SystematicStudyDto
import br.all.application.review.repository.SystematicStudyRepository
import br.all.domain.model.researcher.ResearcherId
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
class FindOneSystematicStudyServiceImplTest {
    @MockK
    private lateinit var systematicStudyRepository: SystematicStudyRepository
    @MockK
    private lateinit var credentialsService: ResearcherCredentialsService
    @MockK
    private lateinit var presenter: FindOneSystematicStudyPresenter
    private lateinit var sut: FindOneSystematicStudyServiceImpl

    @BeforeEach
    fun setUp() {
        sut = FindOneSystematicStudyServiceImpl(systematicStudyRepository, credentialsService)
    }

    @Nested
    @Tag("ValidClasses")
    @DisplayName("When successfully finding one existent systematic study")
    inner class WhenSuccessfullyFindingOneExistentSystematicStudy {
        @Test
        fun `should correctly find a systematic study and prepare a success view`() {
            val researcherId = UUID.randomUUID()
            val systematicStudyId = UUID.randomUUID()
            val dto = SystematicStudyDto(
                systematicStudyId,
                "Title",
                "Description",
                researcherId,
                setOf(researcherId),
            )
            val response = ResponseModel(researcherId, systematicStudyId, dto)

            every { credentialsService.isAuthenticated(ResearcherId(researcherId)) } returns true
            every { credentialsService.hasAuthority(ResearcherId(researcherId)) } returns true
            every { systematicStudyRepository.existsById(systematicStudyId) } returns true
            every { systematicStudyRepository.hasReviewer(systematicStudyId, researcherId) } returns true
            every { systematicStudyRepository.findById(systematicStudyId) } returns dto
            every { presenter.isDone() } returns false
            every { presenter.prepareSuccessView(response) } just Runs

            sut.findById(presenter, researcherId, systematicStudyId)

            verify { presenter.prepareSuccessView(response) }
        }
    }
}
