package br.all.application.review.update.services

import br.all.application.researcher.credentials.ResearcherCredentialsService
import br.all.application.review.repository.SystematicStudyDto
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.review.update.presenter.UpdateSystematicStudyPresenter
import br.all.application.review.util.CredentialsServiceMockBuilder.makeResearcherToBeAllowed
import br.all.application.review.util.CredentialsServiceMockBuilder.makeResearcherToBeUnauthenticated
import br.all.application.review.util.CredentialsServiceMockBuilder.makeResearcherToBeUnauthorized
import br.all.application.review.util.TestDataFactory
import br.all.application.shared.exceptions.EntityNotFoundException
import br.all.application.shared.exceptions.UnauthenticatedUserException
import br.all.application.shared.exceptions.UnauthorizedUserException
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
            makeResearcherToBeAllowed(credentialsService, presenter, factory.researcher)
        }

        @Test
        fun `should only the title be updated`() {
            val dto = factory.generateDto(title = "Old title")
            val updatedDto = dto.copy(title = "New title")
            val response = factory.updateResponseModel()

            makeStudyToBeUpdated(dto, updatedDto, response)

            val request = factory.updateRequestModel("New title")
            sut.update(presenter, factory.researcher, factory.systematicStudy, request)

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
            makeSystematicStudyExist()
            every { repository.findById(factory.systematicStudy) } returns dto
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
            sut.update(presenter, factory.researcher, factory.systematicStudy, request)

            verify {
                repository.saveOrUpdate(updatedDto)
                presenter.prepareSuccessView(response)
            }
        }

        @Test
        fun `should both title and description be updated`() {
            val dto = factory.generateDto(title = "Old title", description = "Old description")
            val updatedDto = dto.copy(title = "New title", description = "New description")
            val response = factory.updateResponseModel()

            makeStudyToBeUpdated(dto, updatedDto, response)

            val request = factory.updateRequestModel("New title", "New description")
            sut.update(presenter, factory.researcher, factory.systematicStudy, request)

            verify {
                repository.saveOrUpdate(updatedDto)
                presenter.prepareSuccessView(response)
            }
        }
    }

    private fun makeSystematicStudyExist() {
        every { repository.existsById(factory.systematicStudy) } returns true
        every { repository.hasReviewer(factory.systematicStudy, factory.researcher) } returns true
    }

    @Nested
    @Tag("InvalidClasses")
    @DisplayName("When the being unable to perform updates")
    inner class WhenTheBeingUnableToPerformUpdates {
        @Test
        fun `should nothing happen when title and description are not given`() {
            val dto = factory.generateDto()
            val response = factory.updateResponseModel()

            makeResearcherToBeAllowed(credentialsService, presenter, factory.researcher)
            makeSystematicStudyExist()
            every { repository.findById(factory.systematicStudy) } returns dto

            val request = factory.updateRequestModel()
            sut.update(presenter, factory.researcher, factory.systematicStudy, request)

            verify(exactly = 0) { repository.saveOrUpdate(any<SystematicStudyDto>()) }
            verify { presenter.prepareSuccessView(response) }
        }

        @Test
        fun `should prepare fail view with EntityNotFoundException when the study does not exist`() {
            val request = factory.updateRequestModel()

            makeResearcherToBeAllowed(credentialsService, presenter, factory.researcher)
            every { repository.existsById(factory.systematicStudy) } returns false
            every { presenter.isDone() } returns false andThen true

            sut.update(presenter, factory.researcher, factory.systematicStudy, request)
            verify(exactly = 2) { presenter.isDone() }
            verify {
                repository.existsById(factory.systematicStudy)
                presenter.prepareFailView(any<EntityNotFoundException>())
            }
        }

        @Test
        fun `should the researcher be unauthorized if they are not a collaborator`() {
            val request = factory.updateRequestModel()

            makeResearcherToBeAllowed(credentialsService, presenter, factory.researcher)
            every { repository.existsById(factory.systematicStudy) } returns true
            every { repository.hasReviewer(factory.systematicStudy, factory.researcher) } returns false
            every { presenter.isDone() } returns false andThen true

            sut.update(presenter, factory.researcher, factory.systematicStudy, request)
            verify {
                repository.hasReviewer(factory.systematicStudy, factory.researcher)
                presenter.prepareFailView(any<UnauthorizedUserException>())
                presenter.isDone()
            }
        }

        @Test
        fun `should prepare fail view if the researcher is unauthenticated`() {
            val request = factory.updateRequestModel()
            makeResearcherToBeUnauthenticated(credentialsService, presenter, factory.researcher)

            sut.update(presenter, factory.researcher, factory.systematicStudy, request)
            verify {
                presenter.prepareFailView(any<UnauthenticatedUserException>())
                presenter.isDone()
            }
        }

        @Test
        fun `should prepare fail view if the researcher is unauthorized`() {
            val request = factory.updateRequestModel()
            makeResearcherToBeUnauthorized(credentialsService, presenter, factory.researcher)

            sut.update(presenter, factory.researcher, factory.systematicStudy, request)
            verify {
                presenter.prepareFailView(any<UnauthorizedUserException>())
                presenter.isDone()
            }
        }
    }
}
