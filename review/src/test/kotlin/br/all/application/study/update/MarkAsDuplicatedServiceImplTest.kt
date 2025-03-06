package br.all.application.study.update

import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.shared.exceptions.EntityNotFoundException
import br.all.application.study.repository.StudyReviewRepository
import br.all.application.study.update.implementation.MarkAsDuplicatedServiceImpl
import br.all.application.study.update.interfaces.MarkAsDuplicatedPresenter
import br.all.application.study.util.TestDataFactory
import br.all.application.user.CredentialsService
import br.all.application.util.PreconditionCheckerMockingNew
import io.mockk.*
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import java.util.*

@Tag("UnitTest")
@Tag("ServiceTest")
@ExtendWith(MockKExtension::class)
class MarkAsDuplicatedServiceImplTest {

    @MockK(relaxed = true) private lateinit var studyReviewRepository: StudyReviewRepository
    @MockK(relaxUnitFun = true) private lateinit var systematicStudyRepository: SystematicStudyRepository
    @MockK private lateinit var credentialService: CredentialsService
    @MockK(relaxed = true) private lateinit var presenter: MarkAsDuplicatedPresenter

    private lateinit var sut: MarkAsDuplicatedServiceImpl

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
        sut = MarkAsDuplicatedServiceImpl(
            systematicStudyRepository,
            studyReviewRepository,
            credentialService,
        )
    }

    @Nested
    @Tag("ValidClasses")
    @DisplayName("When successfully marking Study Reviews as duplicated")
    inner class WhenSuccessfullyMarkingAsDuplicated {
        @Test
        fun `should mark source study as duplicated`() {
            val sourceDto = factory.generateDto()                   // e.g., studyReviewId = 1L
            val destinationDto = factory.generateDto(studyReviewId = 10L)
            // Updated factory method now returns a RequestModel with a duplicatedStudies map,
            // e.g., mapOf(10L to 1L) meaning destination review 10L and source review 1L.
            val request = factory.markAsDuplicatedRequestModel(mapOf(destinationDto.studyReviewId to sourceDto.studyReviewId))

            preconditionCheckerMocking.makeEverythingWork()

            // Extract the pair (destination, source) from the map.
            val (destinationId, sourceId) = request.duplicatedStudies.entries.first()

            every { studyReviewRepository.findById(request.systematicStudyId, sourceId) } returns sourceDto
            every { studyReviewRepository.findById(request.systematicStudyId, destinationId) } returns destinationDto

            sut.markAsDuplicated(presenter, request)

            verify(exactly = 2) { studyReviewRepository.saveOrUpdate(any()) }
            verify(exactly = 1) { presenter.prepareSuccessView(any()) }
        }
    }

    @Nested
    @Tag("InvalidClasses")
    @DisplayName("When failing to mark Study Reviews as duplicated")
    inner class WhenFailingToMarkAsDuplicated {
        @Test
        fun `should not be able to update with non-existent source`() {
            val destinationDto = factory.generateDto(studyReviewId = 10L)
            // Using a source ID that does not exist, e.g., 1L.
            val request = factory.markAsDuplicatedRequestModel(mapOf(destinationDto.studyReviewId to 1L))

            preconditionCheckerMocking.makeEverythingWork()
            val (destinationId, sourceId) = request.duplicatedStudies.entries.first()

            every { studyReviewRepository.findById(request.systematicStudyId, sourceId) } returns null
            every { studyReviewRepository.findById(request.systematicStudyId, destinationId) } returns destinationDto

            sut.markAsDuplicated(presenter, request)

            verify { presenter.prepareFailView(match { it is EntityNotFoundException }) }
        }

        @Test
        fun `should not be able to update with non-existent destination`() {
            val sourceDto = factory.generateDto()  // source DTO, e.g., studyReviewId = 1L
            // Using a destination ID that does not exist, e.g., 10L.
            val request = factory.markAsDuplicatedRequestModel(mapOf(10L to sourceDto.studyReviewId))

            preconditionCheckerMocking.makeEverythingWork()
            val (destinationId, sourceId) = request.duplicatedStudies.entries.first()

            every { studyReviewRepository.findById(request.systematicStudyId, sourceId) } returns sourceDto
            every { studyReviewRepository.findById(request.systematicStudyId, destinationId) } returns null

            sut.markAsDuplicated(presenter, request)

            verify { presenter.prepareFailView(match { it is EntityNotFoundException }) }
        }

        @Test
        fun `should not mark as duplicated when unauthenticated`() {
            val request = factory.markAsDuplicatedRequestModel(mapOf(11L to 1L))

            preconditionCheckerMocking.testForUnauthenticatedUser(presenter, request) { _, _ ->
                sut.markAsDuplicated(presenter, request)
            }
        }

        @Test
        fun `should not mark as duplicated when unauthorized`() {
            val request = factory.markAsDuplicatedRequestModel(mapOf(12L to 1L))

            preconditionCheckerMocking.testForUnauthorizedUser(presenter, request) { _, _ ->
                sut.markAsDuplicated(presenter, request)
            }
        }

        @Test
        fun `should not mark as duplicated when systematic study does not exist`() {
            val request = factory.markAsDuplicatedRequestModel(mapOf(13L to 1L))

            preconditionCheckerMocking.testForNonexistentSystematicStudy(presenter, request) { _, _ ->
                sut.markAsDuplicated(presenter, request)
            }
        }
    }
}
