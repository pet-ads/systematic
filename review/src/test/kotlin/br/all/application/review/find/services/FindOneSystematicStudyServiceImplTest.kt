package br.all.application.review.find.services

import br.all.application.researcher.credentials.ResearcherCredentialsService
import br.all.application.review.find.presenter.FindOneSystematicStudyPresenter
import br.all.application.review.repository.SystematicStudyDto
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.review.util.CredentialsServiceMockBuilder.makeResearcherToBeAllowed
import br.all.application.review.util.TestDataFactory
import br.all.application.review.util.TestDataFactory.findOneResponseModel
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
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
    private lateinit var presenter: FindOneSystematicStudyPresenter
    private lateinit var sut: FindOneSystematicStudyServiceImpl

    @BeforeEach
    fun setUp() {
        presenter = mockk(relaxed = true)
        sut = FindOneSystematicStudyServiceImpl(systematicStudyRepository, credentialsService)
    }

    @Nested
    @Tag("ValidClasses")
    @DisplayName("When successfully finding one existent systematic study")
    inner class WhenSuccessfullyFindingOneExistentSystematicStudy {
        @Test
        fun `should correctly find a systematic study and prepare a success view`() {
            val researcherId = TestDataFactory.researcherId
            val systematicStudyId = TestDataFactory.systematicStudyId
            val response = findOneResponseModel()

            makeResearcherToBeAllowed(credentialsService, presenter, researcherId)
            makeSystematicStudyExist(systematicStudyId, researcherId, response.systematicStudy)

            sut.findById(presenter, researcherId, systematicStudyId)
            verify { presenter.prepareSuccessView(response) }
        }
    }

    private fun makeSystematicStudyExist(
        systematicStudyId: UUID,
        researcherId: UUID,
        dto: SystematicStudyDto
    ) {
        every { systematicStudyRepository.existsById(systematicStudyId) } returns true
        every { systematicStudyRepository.hasReviewer(systematicStudyId, researcherId) } returns true
        every { systematicStudyRepository.findById(systematicStudyId) } returns dto
    }
}
