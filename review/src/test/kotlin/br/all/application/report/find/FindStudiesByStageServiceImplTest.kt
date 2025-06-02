package br.all.application.report.find

import br.all.application.report.find.presenter.FindStudiesByStagePresenter
import br.all.application.report.find.service.FindStudiesByStageService.RequestModel
import br.all.application.report.find.service.FindStudiesByStageService.ResponseModel
import br.all.application.report.find.service.FindStudiesByStageService.StudyCollection
import br.all.application.report.find.service.FindStudiesByStageServiceImpl
import br.all.application.shared.presenter.prepareIfFailsPreconditions
import br.all.application.study.repository.StudyReviewDto
import br.all.application.study.repository.StudyReviewRepository
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.user.CredentialsService
import br.all.application.util.PreconditionCheckerMockingNew
import br.all.domain.model.study.ExtractionStatus
import br.all.domain.model.study.SelectionStatus
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import java.util.*
import kotlin.test.assertTrue

@Tag("UnitTest")
@Tag("ServiceTest")
@ExtendWith(MockKExtension::class)
class FindStudiesByStageServiceImplTest {

    @MockK(relaxUnitFun = true)
    lateinit var presenter: FindStudiesByStagePresenter

    @MockK(relaxUnitFun = true)
    lateinit var systematicStudyRepository: SystematicStudyRepository

    @MockK(relaxUnitFun = true)
    lateinit var studyReviewRepository: StudyReviewRepository

    @MockK
    lateinit var credentialsService: CredentialsService

    @InjectMockKs
    lateinit var sut: FindStudiesByStageServiceImpl

    private lateinit var researcherId: UUID
    private lateinit var systematicStudyId: UUID
    private lateinit var precondition: PreconditionCheckerMockingNew

    @BeforeEach
    fun setUp() {
        researcherId = UUID.randomUUID()
        systematicStudyId = UUID.randomUUID()
        precondition = PreconditionCheckerMockingNew(
            presenter,
            credentialsService,
            systematicStudyRepository,
            researcherId,
            systematicStudyId
        )
        precondition.makeEverythingWork()
    }

    @Nested
    @DisplayName("When being successful")
    inner class WhenBeingSuccessful {

        @Test
        fun `should return correct response for selection stage`() {
            val study1 = mockk<StudyReviewDto>(relaxed = true)
            every { study1.studyReviewId } returns 1L
            every { study1.selectionStatus } returns SelectionStatus.INCLUDED.name
            every { study1.extractionStatus } returns ExtractionStatus.UNCLASSIFIED.name

            val study2 = mockk<StudyReviewDto>(relaxed = true)
            every { study2.studyReviewId } returns 2L
            every { study2.selectionStatus } returns SelectionStatus.EXCLUDED.name
            every { study2.extractionStatus } returns ExtractionStatus.UNCLASSIFIED.name

            val study3 = mockk<StudyReviewDto>(relaxed = true)
            every { study3.studyReviewId } returns 3L
            every { study3.selectionStatus } returns SelectionStatus.UNCLASSIFIED.name
            every { study3.extractionStatus } returns ExtractionStatus.UNCLASSIFIED.name

            val study4 = mockk<StudyReviewDto>(relaxed = true)
            every { study4.studyReviewId } returns 4L
            every { study4.selectionStatus } returns SelectionStatus.DUPLICATED.name
            every { study4.extractionStatus } returns ExtractionStatus.UNCLASSIFIED.name

            every { studyReviewRepository.findAllFromReview(systematicStudyId) } returns
                    listOf(study1, study2, study3, study4)

            val request = RequestModel(
                userId = researcherId,
                systematicStudyId = systematicStudyId,
                stage = "selection"
            )

            sut.findStudiesByStage(presenter, request)

            val expectedIncluded = StudyCollection(listOf(1L), 1)
            val expectedExcluded = StudyCollection(listOf(2L), 1)
            val expectedUnclassified = StudyCollection(listOf(3L), 1)
            val expectedDuplicated = StudyCollection(listOf(4L), 1)

            val expectedResponse = ResponseModel(
                userId = researcherId,
                systematicStudyId = systematicStudyId,
                stage = "selection",
                included = expectedIncluded,
                excluded = expectedExcluded,
                unclassified = expectedUnclassified,
                duplicated = expectedDuplicated
            )

            verify(exactly = 1) {
                presenter.prepareSuccessView(expectedResponse)
            }
        }

        @Test
        fun `should return correct response for extraction stage`() {
            val study1 = mockk<StudyReviewDto>(relaxed = true)
            every { study1.studyReviewId } returns 10L
            every { study1.selectionStatus } returns SelectionStatus.UNCLASSIFIED.name
            every { study1.extractionStatus } returns ExtractionStatus.INCLUDED.name

            val study2 = mockk<StudyReviewDto>(relaxed = true)
            every { study2.studyReviewId } returns 20L
            every { study2.selectionStatus } returns SelectionStatus.UNCLASSIFIED.name
            every { study2.extractionStatus } returns ExtractionStatus.EXCLUDED.name

            val study3 = mockk<StudyReviewDto>(relaxed = true)
            every { study3.studyReviewId } returns 30L
            every { study3.selectionStatus } returns SelectionStatus.UNCLASSIFIED.name
            every { study3.extractionStatus } returns ExtractionStatus.UNCLASSIFIED.name

            val study4 = mockk<StudyReviewDto>(relaxed = true)
            every { study4.studyReviewId } returns 40L
            every { study4.selectionStatus } returns SelectionStatus.UNCLASSIFIED.name
            every { study4.extractionStatus } returns ExtractionStatus.DUPLICATED.name

            every { studyReviewRepository.findAllFromReview(systematicStudyId) } returns
                    listOf(study1, study2, study3, study4)

            val request = RequestModel(
                userId = researcherId,
                systematicStudyId = systematicStudyId,
                stage = "extraction"
            )

            sut.findStudiesByStage(presenter, request)

            val expectedIncluded = StudyCollection(listOf(10L), 1)
            val expectedExcluded = StudyCollection(listOf(20L), 1)
            val expectedUnclassified = StudyCollection(listOf(30L), 1)
            val expectedDuplicated = StudyCollection(listOf(40L), 1)

            val expectedResponse = ResponseModel(
                userId = researcherId,
                systematicStudyId = systematicStudyId,
                stage = "extraction",
                included = expectedIncluded,
                excluded = expectedExcluded,
                unclassified = expectedUnclassified,
                duplicated = expectedDuplicated
            )

            verify(exactly = 1) {
                presenter.prepareSuccessView(expectedResponse)
            }
        }
    }

    @Nested
    @DisplayName("When being unsuccessful")
    inner class WhenBeingUnsuccessful {

        @Test
        fun `should fail when there is no systematic study`() {
            precondition.makeSystematicStudyNonexistent()

            val request = RequestModel(
                userId = researcherId,
                systematicStudyId = systematicStudyId,
                stage = "selection"
            )

            every { studyReviewRepository.findAllFromReview(systematicStudyId) } returns emptyList()

            sut.findStudiesByStage(presenter, request)

            verify(exactly = 1) {
                presenter.prepareIfFailsPreconditions(any(), any())
            }
            assertTrue { presenter.isDone() }
        }

        @Test
        fun `should fail when there is no user`() {
            precondition.makeUserUnauthenticated()

            val request = RequestModel(
                userId = researcherId,
                systematicStudyId = systematicStudyId,
                stage = "extraction"
            )

            sut.findStudiesByStage(presenter, request)

            verify(exactly = 1) {
                presenter.prepareIfFailsPreconditions(any(), any())
            }
            assertTrue { presenter.isDone() }
        }
    }
}
