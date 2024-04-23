package br.all.application.study.update

import br.all.application.researcher.credentials.ResearcherCredentialsService
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.shared.exceptions.EntityNotFoundException
import br.all.application.study.repository.StudyReviewRepository
import br.all.application.study.update.implementation.UpdateStudyReviewSelectionService
import br.all.application.study.update.interfaces.UpdateStudyReviewStatusPresenter
import br.all.application.study.util.TestDataFactory
import br.all.application.util.PreconditionCheckerMocking
import io.mockk.*
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
@Tag("UnitTest")
@Tag("ServiceTest")
@ExtendWith(MockKExtension::class)
class UpdateStudyReviewSelectionStatusServiceTest {

    @MockK(relaxed = true) private lateinit var studyReviewRepository: StudyReviewRepository
    @MockK(relaxUnitFun = true) private lateinit var systematicStudyRepository: SystematicStudyRepository
    @MockK private lateinit var credentialService: ResearcherCredentialsService
    @MockK(relaxed = true) private lateinit var presenter: UpdateStudyReviewStatusPresenter

    private lateinit var sut: UpdateStudyReviewSelectionService

    private lateinit var factory: TestDataFactory
    private lateinit var preconditionCheckerMocking: PreconditionCheckerMocking

    @BeforeEach
    fun setUp() {
        factory = TestDataFactory()
        preconditionCheckerMocking = PreconditionCheckerMocking(
            presenter,
            credentialService,
            systematicStudyRepository,
            factory.researcherId,
            factory.systematicStudyId
        )
        preconditionCheckerMocking.makeEverythingWork()
        sut = UpdateStudyReviewSelectionService(
            systematicStudyRepository,
            studyReviewRepository,
            credentialService,
        )
    }

    @Nested
    @Tag("ValidClasses")
    @DisplayName("When successfully updating a Study Review's selection status")
    inner class WhenSuccessfullyUpdatingAStudyReviewSelection {
        @Test
        fun `should successfully update a Study Review's selection status`() {
            val dto = factory.generateDto()
            val request = factory.updateStatusRequestModel("INCLUDED")

            every { studyReviewRepository.findById(request.systematicStudyId, request.studyReviewId) } returns dto

            sut.changeStatus(presenter, request)

            verify(exactly = 1) {
                studyReviewRepository.saveOrUpdate(any())
                presenter.prepareSuccessView(any())
            }
        }
    }

    @Nested
    @Tag("InvalidClasses")
    @DisplayName("When failing to update a study review's selection status")
    inner class WhenFailingToUpdateAStudyReviewSelection {
        @Test
        fun `should not be able to update a non-existent study`() {
            val request = factory.updateStatusRequestModel("INCLUDED")

            every { studyReviewRepository.findById(request.systematicStudyId, request.studyReviewId) } returns null
            sut.changeStatus(presenter, request)

            verify {
                presenter.prepareFailView(any<EntityNotFoundException>())
            }
        }

        @Test
        fun `should not accept duplicated as a new status`() {
            val dto = factory.generateDto()
            val request = factory.updateStatusRequestModel("DUPLICATED")

            every { studyReviewRepository.findById(request.systematicStudyId, request.studyReviewId) } returns dto
            sut.changeStatus(presenter, request)

            verify {
                presenter.prepareFailView(any<IllegalArgumentException>())
            }
        }

        @Test
        fun `should not accept invalid statuses`() {
            val dto = factory.generateDto()
            val request = factory.updateStatusRequestModel("NOTREAL")

            every { studyReviewRepository.findById(request.systematicStudyId, request.studyReviewId) } returns dto
            sut.changeStatus(presenter, request)

            verify {
                presenter.prepareFailView(any<IllegalArgumentException>())
            }
        }
    }

}