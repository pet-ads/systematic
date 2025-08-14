package br.all.application.study.update

import br.all.application.review.repository.SystematicStudyRepository
import br.all.domain.shared.exception.EntityNotFoundException
import br.all.application.study.repository.StudyReviewRepository
import br.all.application.study.update.implementation.UpdateStudyReviewSelectionService
import br.all.application.study.update.interfaces.UpdateStudyReviewStatusPresenter
import br.all.application.study.util.TestDataFactory
import br.all.application.user.CredentialsService
import br.all.application.util.PreconditionCheckerMockingNew
import io.mockk.*
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import kotlin.test.assertFailsWith

@Tag("UnitTest")
@Tag("ServiceTest")
@ExtendWith(MockKExtension::class)
class UpdateStudyReviewSelectionStatusServiceTest {

    @MockK(relaxed = true) private lateinit var studyReviewRepository: StudyReviewRepository
    @MockK(relaxUnitFun = true) private lateinit var systematicStudyRepository: SystematicStudyRepository
    @MockK private lateinit var credentialService: CredentialsService
    @MockK(relaxed = true) private lateinit var presenter: UpdateStudyReviewStatusPresenter

    private lateinit var sut: UpdateStudyReviewSelectionService

    private lateinit var factory: TestDataFactory
    private lateinit var preconditionCheckerMocking: PreconditionCheckerMockingNew

    @BeforeEach
    fun setUp() {
        factory = TestDataFactory()
        preconditionCheckerMocking = PreconditionCheckerMockingNew(
            presenter,
            credentialService,
            systematicStudyRepository,
            factory.researcherId,
            factory.systematicStudyId
        )
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
            val request = factory.updateStatusRequestModel("INCLUDED", setOf("criteria1", "criteria2"))

            preconditionCheckerMocking.makeEverythingWork()

            every { studyReviewRepository.findById(request.systematicStudyId, request.studyReviewId.first()) } returns dto

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
            val request = factory.updateStatusRequestModel("INCLUDED", setOf("criteria1", "criteria2"))

            preconditionCheckerMocking.makeEverythingWork()

            every { studyReviewRepository.findById(request.systematicStudyId, request.studyReviewId.first()) } returns null
            sut.changeStatus(presenter, request)

            verify {
                presenter.prepareFailView(any<EntityNotFoundException>())
            }
        }

        @Test
        fun `should not accept duplicated as a new status`() {
            val dto = factory.generateDto()
            val request = factory.updateStatusRequestModel("DUPLICATED", setOf("criteria1", "criteria2"))

            preconditionCheckerMocking.makeEverythingWork()

            every { studyReviewRepository.findById(request.systematicStudyId, request.studyReviewId.first()) } returns dto
            sut.changeStatus(presenter, request)

            verify {
                presenter.prepareFailView(any<IllegalArgumentException>())
            }
        }

        @Test
        fun `should not accept invalid statuses`() {
            val dto = factory.generateDto()
            val request = factory.updateStatusRequestModel("NOTREAL", setOf("criteria1", "criteria2"))

            preconditionCheckerMocking.makeEverythingWork()

            every { studyReviewRepository.findById(request.systematicStudyId, request.studyReviewId.first()) } returns dto

            assertFailsWith<IllegalArgumentException> {
                sut.changeStatus(presenter, request)
            }
        }

        @Test
        fun `should not update when unauthenticated`() {
            val request = factory.updateStatusRequestModel("status!", setOf("criteria1", "criteria2"))

            preconditionCheckerMocking.testForUnauthenticatedUser(presenter, request) { _, _ ->
                sut.changeStatus(presenter, request)
            }

        }

        @Test
        fun `should not update when unauthorized`() {
            val request = factory.updateStatusRequestModel("status!", setOf("criteria1", "criteria2"))

            preconditionCheckerMocking.testForUnauthorizedUser(presenter, request) { _, _ ->
                sut.changeStatus(presenter, request)
            }

        }

        @Test
        fun `should not update when systematic study doesnt exist`() {
            val request = factory.updateStatusRequestModel("status!", setOf("criteria1", "criteria2"))

            preconditionCheckerMocking.testForNonexistentSystematicStudy(presenter, request) { _, _ ->
                sut.changeStatus(presenter, request)
            }

        }
    }

}