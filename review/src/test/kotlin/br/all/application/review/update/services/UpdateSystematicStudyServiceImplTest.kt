package br.all.application.review.update.services

import br.all.application.researcher.credentials.ResearcherCredentialsService
import br.all.application.review.repository.SystematicStudyDto
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.review.update.presenter.UpdateSystematicStudyPresenter
import br.all.application.review.util.CredentialsServiceMockBuilder.makeResearcherToBeAllowed
import br.all.application.review.util.TestDataFactory
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.verify
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith

@Tag("UnitTest")
@Tag("ServiceTest")
@ExtendWith(MockKExtension::class)
class UpdateSystematicStudyServiceImplTest {
    @MockK
    private lateinit var repository: SystematicStudyRepository
    @MockK
    private lateinit var credentialsService: ResearcherCredentialsService
    @MockK(relaxed = true)
    private lateinit var presenter: UpdateSystematicStudyPresenter
    private lateinit var factory: TestDataFactory
    private lateinit var sut: UpdateSystematicStudyServiceImpl

    @BeforeEach
    fun setUp() {
        factory = TestDataFactory()
        sut = UpdateSystematicStudyServiceImpl(repository, credentialsService)
    }

    @Nested
    @Tag("ValidClasses")
    @DisplayName("When the systematic study is updated")
    inner class WhenTheSystematicStudyIsUpdated {
        @BeforeEach
        fun setUp() {
            makeResearcherToBeAllowed(credentialsService, presenter, factory.researcherId)
        }

        @Test
        fun `should only the title be updated`() {
            val dto = factory.generateDto(title = "Old title")
            val updatedDto = dto.copy(title = "New title")
            val response = factory.updateResponseModel()

            makeStudyToBeUpdated(dto, updatedDto, response)

            val request = factory.updateRequestModel("New title")
            sut.update(presenter, factory.researcherId, factory.systematicStudyId, request)

            verify {
                repository.saveOrUpdate(updatedDto)
                presenter.prepareSuccessView(response)
            }
        }

        private fun makeStudyToBeUpdated(
            dto: SystematicStudyDto,
            updatedDto: SystematicStudyDto,
            response: UpdateSystematicStudyService.ResponseModel
        ) {
            every { repository.existsById(factory.systematicStudyId) } returns true
            every { repository.hasReviewer(factory.systematicStudyId, factory.researcherId) } returns true
            every { repository.findById(factory.systematicStudyId) } returns dto
            every { repository.saveOrUpdate(updatedDto) } just Runs
            every { presenter.prepareSuccessView(response) } just Runs
        }

        @Test
        fun `should only the description be updated`() {
            val dto = factory.generateDto(description = "Old description")
            val updatedDto = dto.copy(description = "New description")
            val response = factory.updateResponseModel()

            makeStudyToBeUpdated(dto, updatedDto, response)

            val request = factory.updateRequestModel(description = "New description")
            sut.update(presenter, factory.researcherId, factory.systematicStudyId, request)

            verify {
                repository.saveOrUpdate(updatedDto)
                presenter.prepareSuccessView(response)
            }
        }
    }
}
