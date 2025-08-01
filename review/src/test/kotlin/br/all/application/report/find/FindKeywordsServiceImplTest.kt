package br.all.application.report.find

import br.all.application.collaboration.repository.CollaborationRepository
import br.all.application.report.find.presenter.FindKeywordsPresenter
import br.all.application.report.find.service.FindKeywordsService
import br.all.application.report.find.service.FindKeywordsServiceImpl
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.study.repository.StudyReviewRepository
import br.all.application.user.CredentialsService
import br.all.application.util.PreconditionCheckerMockingNew
import br.all.application.study.util.TestDataFactory as StudyReviewFactory
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.UUID

@Tag("UnitTest")
@Tag("ServiceTest")
@ExtendWith(MockKExtension::class)
class FindKeywordsServiceImplTest {

    @MockK(relaxUnitFun = true)
    private lateinit var systematicStudyRepository: SystematicStudyRepository

    @MockK(relaxUnitFun = true)
    private lateinit var studyReviewRepository: StudyReviewRepository

    @MockK
    private lateinit var credentialsService: CredentialsService

    @MockK(relaxUnitFun = true)
    private lateinit var presenter: FindKeywordsPresenter

    @MockK
    private lateinit var collaborationRepository: CollaborationRepository

    @InjectMockKs
    private lateinit var sut: FindKeywordsServiceImpl

    private lateinit var precondition: PreconditionCheckerMockingNew
    private lateinit var factory: StudyReviewFactory

    private lateinit var researcherId: UUID
    private lateinit var systematicStudyId: UUID

    @BeforeEach
    fun setup() {
        factory = StudyReviewFactory()

        researcherId = factory.researcherId
        systematicStudyId = factory.systematicStudyId

        precondition = PreconditionCheckerMockingNew(
            presenter,
            credentialsService,
            systematicStudyRepository,
            collaborationRepository,
            researcherId,
            systematicStudyId,
            UUID.randomUUID()
        )
        precondition.makeEverythingWork()
    }

    @Nested
    @DisplayName("When filtering by selection")
    inner class FilterBySelection {
        @Test
        fun `should count only INCLUDED selection keywords`() {
            val dto1 = factory.generateDto(
                selectionStatus = "INCLUDED",
                extractionStatus = "EXCLUDED",
                keywords = setOf("key1;key2")
            )
            val dto2 = factory.generateDto(
                selectionStatus = "INCLUDED",
                extractionStatus = "INCLUDED",
                keywords = setOf("key1;key3")
            )
            val dto3 = factory.generateDto(
                selectionStatus = "EXCLUDED",
                extractionStatus = "EXCLUDED",
                keywords = setOf("key1;key4")
            )
            every { studyReviewRepository.findAllFromReview(systematicStudyId) }
                .returns(listOf(dto1, dto2, dto3))

            val request = FindKeywordsService.RequestModel(
                userId = researcherId,
                systematicStudyId = systematicStudyId,
                filter = "selection"
            )

            sut.findKeywords(presenter, request)

            val expectedKeywords = mapOf(
                "key1" to 2,
                "key2" to 1,
                "key3" to 1
            )

            val expectedResponse = FindKeywordsService.ResponseModel(
                userId = researcherId,
                systematicStudyId = systematicStudyId,
                filter = "selection",
                keywords = expectedKeywords,
                keywordsQuantity = expectedKeywords.values.sum()
            )

            verify(exactly = 1) {
                presenter.prepareSuccessView(expectedResponse)
            }
        }
    }

    @Nested
    @DisplayName("When filtering by extraction")
    inner class FilterByExtraction {
        @Test
        fun `should count only INCLUDED extraction keywords`() {
            val dto1 = factory.generateDto(
                selectionStatus = "INCLUDED",
                extractionStatus = "INCLUDED",
                keywords = setOf("a;b")
            )
            val dto2 = factory.generateDto(
                selectionStatus = "EXCLUDED",
                extractionStatus = "INCLUDED",
                keywords = setOf("b;c")
            )
            val dto3 = factory.generateDto(
                selectionStatus = "INCLUDED",
                extractionStatus = "EXCLUDED",
                keywords = setOf("a;d")
            )
            every { studyReviewRepository.findAllFromReview(systematicStudyId) }
                .returns(listOf(dto1, dto2, dto3))

            val request = FindKeywordsService.RequestModel(
                userId = researcherId,
                systematicStudyId = systematicStudyId,
                filter = "extraction"
            )

            sut.findKeywords(presenter, request)

            val expectedKeywords = mapOf(
                "a" to 1,
                "b" to 2,
                "c" to 1
            )

            val expectedResponse = FindKeywordsService.ResponseModel(
                userId = researcherId,
                systematicStudyId = systematicStudyId,
                filter = "extraction",
                keywords = expectedKeywords,
                keywordsQuantity = expectedKeywords.values.sum()
            )

            verify(exactly = 1) {
                presenter.prepareSuccessView(expectedResponse)
            }
        }
    }

    @Nested
    @DisplayName("When no filter (null)")
    inner class NoFilter {
        @Test
        fun `should count all keywords across studies`() {
            val dto1 = factory.generateDto(
                selectionStatus = "INCLUDED",
                extractionStatus = "INCLUDED",
                keywords = setOf("x;y")
            )
            val dto2 = factory.generateDto(
                selectionStatus = "EXCLUDED",
                extractionStatus = "EXCLUDED",
                keywords = setOf("y;z")
            )
            every { studyReviewRepository.findAllFromReview(systematicStudyId) }
                .returns(listOf(dto1, dto2))

            val request = FindKeywordsService.RequestModel(
                userId = researcherId,
                systematicStudyId = systematicStudyId,
                filter = null
            )

            sut.findKeywords(presenter, request)

            val expectedKeywords = mapOf(
                "x" to 1,
                "y" to 2,
                "z" to 1
            )

            val expectedResponse = FindKeywordsService.ResponseModel(
                userId = researcherId,
                systematicStudyId = systematicStudyId,
                filter = null,
                keywords = expectedKeywords,
                keywordsQuantity = expectedKeywords.values.sum()
            )

            verify(exactly = 1) {
                presenter.prepareSuccessView(expectedResponse)
            }
        }
    }
}