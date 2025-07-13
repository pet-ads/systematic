package br.all.application.report.find

import br.all.application.report.find.presenter.StudiesFunnelPresenter
import br.all.application.report.find.service.StudiesFunnelService
import br.all.application.report.find.service.StudiesFunnelServiceImpl
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.study.repository.StudyReviewDto
import br.all.application.study.repository.StudyReviewRepository
import br.all.application.shared.presenter.prepareIfFailsPreconditions
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
class StudiesFunnelServiceImplTest {

    @MockK(relaxUnitFun = true)
    lateinit var presenter: StudiesFunnelPresenter

    @MockK(relaxUnitFun = true)
    lateinit var systematicStudyRepository: SystematicStudyRepository

    @MockK(relaxUnitFun = true)
    lateinit var studyReviewRepository: StudyReviewRepository

    @MockK
    lateinit var credentialsService: CredentialsService

    @InjectMockKs
    lateinit var sut: StudiesFunnelServiceImpl

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
        fun `should return correct response for multiple studies`() {
            val review1 = mockk<StudyReviewDto>(relaxed = true)
            every { review1.searchSources } returns setOf("S1", "S2")
            every { review1.selectionStatus } returns SelectionStatus.EXCLUDED.name
            every { review1.extractionStatus } returns ExtractionStatus.INCLUDED.name
            every { review1.criteria } returns setOf("Crit1", "Crit2")

            val review2 = mockk<StudyReviewDto>(relaxed = true)
            every { review2.searchSources } returns setOf("S2")
            every { review2.selectionStatus } returns SelectionStatus.INCLUDED.name
            every { review2.extractionStatus } returns ExtractionStatus.EXCLUDED.name
            every { review2.criteria } returns setOf("Crit2", "Crit3")

            val review3 = mockk<StudyReviewDto>(relaxed = true)
            every { review3.searchSources } returns setOf("S1")
            every { review3.selectionStatus } returns SelectionStatus.INCLUDED.name
            every { review3.extractionStatus } returns ExtractionStatus.INCLUDED.name
            every { review3.criteria } returns setOf("Crit3")

            val review4 = mockk<StudyReviewDto>(relaxed = true)
            every { review4.searchSources } returns setOf("S3")
            every { review4.selectionStatus } returns SelectionStatus.DUPLICATED.name
            every { review4.extractionStatus } returns ExtractionStatus.INCLUDED.name
            every { review4.criteria } returns setOf("Crit4")

            every { studyReviewRepository.findAllFromReview(systematicStudyId) } returns
                    listOf(review1, review2, review3, review4)

            val request = StudiesFunnelService.RequestModel(
                userId = researcherId,
                systematicStudyId = systematicStudyId
            )

            sut.studiesFunnel(presenter, request)

            val totalIdentifiedBySource = mapOf(
                "S1" to 2,
                "S2" to 2,
                "S3" to 1
            )
            val totalAfterDuplicatesRemovedBySource = mapOf(
                "S1" to 2,
                "S2" to 2
            )
            val totalScreened = 4
            val totalExcludedInScreening = 1
            val excludedByCriterion = mapOf(
                "Crit1" to 1,
                "Crit2" to 1
            )
            val totalFullTextAssessed = 2
            val totalExcludedInFullText = 1
            val totalExcludedByCriterion = mapOf(
                "Crit2" to 1,
                "Crit3" to 1
            )
            val totalIncluded = 1

            val expectedResponse = StudiesFunnelService.ResponseModel(
                userId = researcherId,
                systematicStudyId = systematicStudyId,
                totalIdentifiedBySource = totalIdentifiedBySource,
                totalAfterDuplicatesRemovedBySource = totalAfterDuplicatesRemovedBySource,
                totalScreened = totalScreened,
                totalExcludedInScreening = totalExcludedInScreening,
                excludedByCriterion = excludedByCriterion,
                totalFullTextAssessed = totalFullTextAssessed,
                totalExcludedInFullText = totalExcludedInFullText,
                totalExcludedByCriterion = totalExcludedByCriterion,
                totalIncluded = totalIncluded
            )

            verify(exactly = 1) {
                presenter.prepareSuccessView(expectedResponse)
            }
        }

        @Test
        fun `should return zeroed response when there are no studies`() {
            every { studyReviewRepository.findAllFromReview(systematicStudyId) } returns emptyList()

            val request = StudiesFunnelService.RequestModel(
                userId = researcherId,
                systematicStudyId = systematicStudyId
            )

            sut.studiesFunnel(presenter, request)

            val expectedResponse = StudiesFunnelService.ResponseModel(
                userId = researcherId,
                systematicStudyId = systematicStudyId,
                totalIdentifiedBySource = emptyMap(),
                totalAfterDuplicatesRemovedBySource = emptyMap(),
                totalScreened = 0,
                totalExcludedInScreening = 0,
                excludedByCriterion = emptyMap(),
                totalFullTextAssessed = 0,
                totalExcludedInFullText = 0,
                totalExcludedByCriterion = emptyMap(),
                totalIncluded = 0
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
        fun `should fail view when there is no systematic study`() {
            precondition.makeSystematicStudyNonexistent()

            val request = StudiesFunnelService.RequestModel(
                userId = researcherId,
                systematicStudyId = systematicStudyId
            )

            every { studyReviewRepository.findAllFromReview(systematicStudyId) } returns emptyList()

            sut.studiesFunnel(presenter, request)

            verify(exactly = 1) {
                presenter.prepareIfFailsPreconditions(any(), any(),)
            }
            assertTrue { presenter.isDone() }
        }

        @Test
        fun `should fail view when there is no user`() {
            precondition.makeUserUnauthenticated()

            val dummyReview = mockk<StudyReviewDto>(relaxed = true)
            every { studyReviewRepository.findAllFromReview(systematicStudyId) } returns listOf(dummyReview)

            val request = StudiesFunnelService.RequestModel(
                userId = researcherId,
                systematicStudyId = systematicStudyId
            )

            sut.studiesFunnel(presenter, request)

            verify(exactly = 1) {
                presenter.prepareIfFailsPreconditions(any(), any(),)
            }
            assertTrue { presenter.isDone() }
        }
    }
}
