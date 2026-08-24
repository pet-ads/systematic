package br.all.application.report.find

import br.all.application.protocol.repository.CriterionDto
import br.all.application.protocol.repository.ProtocolRepository
import br.all.application.report.find.presenter.FindCriteriaPresenter
import br.all.application.report.find.service.FindCriteriaService
import br.all.application.report.find.service.FindCriteriaServiceImpl
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.study.repository.StudyReviewRepository
import br.all.application.user.CredentialsService
import br.all.application.util.PreconditionCheckerMockingNew
import br.all.domain.model.study.StudyReviewStage
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.extension.ExtendWith
import java.util.UUID
import kotlin.test.Test
import br.all.application.study.util.TestDataFactory as StudyReviewFactory
import br.all.application.report.util.TestDataFactory as CriteriaFactory
import br.all.application.protocol.util.TestDataFactory as ProtocolDtoFactory


@Tag("UnitTest")
@Tag("ServiceTest")
@ExtendWith(MockKExtension::class)
class FindCriteriaServiceImplTest {

    @MockK(relaxUnitFun = true)
    private lateinit var protocolRepository: ProtocolRepository

    @MockK(relaxUnitFun = true)
    private lateinit var systematicStudyRepository: SystematicStudyRepository

    @MockK
    private lateinit var credentialsService: CredentialsService

    @MockK(relaxUnitFun = true)
    private lateinit var studyReviewRepository: StudyReviewRepository

    @MockK(relaxUnitFun = true)
    private lateinit var presenter: FindCriteriaPresenter

    @InjectMockKs
    private lateinit var sut: FindCriteriaServiceImpl


    private lateinit var precondition: PreconditionCheckerMockingNew
    private lateinit var studyFactory: StudyReviewFactory
    private lateinit var criteriaFactory: CriteriaFactory
    private lateinit var protocolDtoFactory: ProtocolDtoFactory

    private lateinit var researcherId: UUID
    private lateinit var systematicStudyId: UUID


    @BeforeEach
    fun setup() {
        studyFactory = StudyReviewFactory()
        criteriaFactory = CriteriaFactory()
        protocolDtoFactory = ProtocolDtoFactory()

        researcherId = studyFactory.researcherId
        systematicStudyId = studyFactory.systematicStudyId

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
    @DisplayName("When successfully finding criteria")
    inner class SuccessfullyFindingCriteria {


        @Test
        fun `should return selection criteria with associated studies`() {

            val inclusionCriterion1 = criteriaFactory.criteriaDto(
                description = "criterion1",
                type = "INCLUSION"
            )

            val inclusionCriterion2 = criteriaFactory.criteriaDto(
                description = "criterion2",
                type = "INCLUSION"
            )

            val exclusionCriterion = criteriaFactory.criteriaDto(
                description = "exclusion1",
                type = "EXCLUSION"
            )


            val protocolDto = protocolDtoFactory.protocolDto(
                systematicStudy = systematicStudyId,
                eligibilityCriteria = setOf(
                    inclusionCriterion1,
                    inclusionCriterion2,
                    exclusionCriterion
                )
            )


            val studyReview1 = studyFactory.generateDto(
                systematicStudyId = systematicStudyId,
                selectionCriteria = setOf("criterion1")
            )

            val studyReview2 = studyFactory.generateDto(
                systematicStudyId = systematicStudyId,
                selectionCriteria = setOf("criterion1", "criterion2")
            )

            val studyReview3 = studyFactory.generateDto(
                systematicStudyId = systematicStudyId,
                selectionCriteria = setOf("exclusion1")
            )


            every {
                protocolRepository.findById(systematicStudyId)
            } returns protocolDto

            every {
                studyReviewRepository.findAllFromReview(systematicStudyId)
            } returns listOf(
                studyReview1,
                studyReview2,
                studyReview3
            )


            val request = FindCriteriaService.RequestModel(
                userId = researcherId,
                systematicStudyId = systematicStudyId,
                type = "INCLUSION",
                stage = StudyReviewStage.SELECTION
            )


            sut.findCriteria(presenter, request)


            val expectedCriteriaMap: Map<CriterionDto, List<Long>> = mapOf(
                inclusionCriterion1 to listOf(
                    studyReview1.studyReviewId,
                    studyReview2.studyReviewId
                ),
                inclusionCriterion2 to listOf(
                    studyReview2.studyReviewId
                )
            )


            val expectedResponse = FindCriteriaService.ResponseModel(
                userId = researcherId,
                systematicStudyId = systematicStudyId,
                stage = StudyReviewStage.SELECTION,
                criteria = FindCriteriaService.FoundStudies(
                    included = expectedCriteriaMap,
                )
            )


            verify(exactly = 1) {
                presenter.prepareSuccessView(expectedResponse)
            }
        }



        @Test
        fun `should return extraction criteria with associated studies`() {

            val extractionCriterion = criteriaFactory.criteriaDto(
                description = "extraction1",
                type = "EXCLUSION"
            )


            val protocolDto = protocolDtoFactory.protocolDto(
                systematicStudy = systematicStudyId,
                eligibilityCriteria = setOf(extractionCriterion)
            )


            val studyReview1 = studyFactory.generateDto(
                systematicStudyId = systematicStudyId,
                selectionCriteria = setOf("criterion1"),
                extractionCriteria = setOf("extraction1")
            )


            val studyReview2 = studyFactory.generateDto(
                systematicStudyId = systematicStudyId,
                selectionCriteria = setOf("criterion2"),
                extractionCriteria = setOf("extraction1")
            )


            every {
                protocolRepository.findById(systematicStudyId)
            } returns protocolDto


            every {
                studyReviewRepository.findAllFromReview(systematicStudyId)
            } returns listOf(
                studyReview1,
                studyReview2
            )


            val request = FindCriteriaService.RequestModel(
                userId = researcherId,
                systematicStudyId = systematicStudyId,
                type = "EXCLUSION",
                stage = StudyReviewStage.EXTRACTION
            )


            sut.findCriteria(presenter, request)


            val expectedCriteriaMap: Map<CriterionDto, List<Long>> = mapOf(
                extractionCriterion to listOf(
                    studyReview1.studyReviewId,
                    studyReview2.studyReviewId
                )
            )


            val expectedResponse = FindCriteriaService.ResponseModel(
                userId = researcherId,
                systematicStudyId = systematicStudyId,
                stage = StudyReviewStage.EXTRACTION,
                criteria = FindCriteriaService.FoundStudies(
                    included = expectedCriteriaMap
                )
            )


            verify(exactly = 1) {
                presenter.prepareSuccessView(expectedResponse)
            }
        }



        @Test
        fun `should not return extraction criteria when searching selection criteria`() {

            val selectionCriterion = criteriaFactory.criteriaDto(
                description = "selectionCriterion",
                type = "EXCLUSION"
            )

            val extractionCriterion = criteriaFactory.criteriaDto(
                description = "extractionCriterion",
                type = "EXCLUSION"
            )


            val protocolDto = protocolDtoFactory.protocolDto(
                systematicStudy = systematicStudyId,
                eligibilityCriteria = setOf(
                    selectionCriterion,
                    extractionCriterion
                )
            )


            val studyReview = studyFactory.generateDto(
                systematicStudyId = systematicStudyId,
                selectionCriteria = setOf("selectionCriterion"),
                extractionCriteria = setOf("extractionCriterion")
            )


            every {
                protocolRepository.findById(systematicStudyId)
            } returns protocolDto


            every {
                studyReviewRepository.findAllFromReview(systematicStudyId)
            } returns listOf(studyReview)


            val request = FindCriteriaService.RequestModel(
                userId = researcherId,
                systematicStudyId = systematicStudyId,
                type = "EXCLUSION",
                stage = StudyReviewStage.SELECTION
            )


            sut.findCriteria(presenter, request)


            val expectedResponse = FindCriteriaService.ResponseModel(
                userId = researcherId,
                systematicStudyId = systematicStudyId,
                stage = StudyReviewStage.SELECTION,
                criteria = FindCriteriaService.FoundStudies(
                    included = mapOf(
                        selectionCriterion to listOf(
                            studyReview.studyReviewId
                        ),
                        extractionCriterion to emptyList()
                    )
                )
            )


            verify(exactly = 1) {
                presenter.prepareSuccessView(expectedResponse)
            }
        }
    }



    @Nested
    @DisplayName("When failing to find criteria")
    inner class FailingFindingCriteria {


        @Test
        fun `should return empty results when protocol is not found`() {

            every {
                protocolRepository.findById(systematicStudyId)
            } returns null


            every {
                studyReviewRepository.findAllFromReview(systematicStudyId)
            } returns emptyList()


            val request = FindCriteriaService.RequestModel(
                userId = researcherId,
                systematicStudyId = systematicStudyId,
                type = "INCLUSION",
                stage = StudyReviewStage.SELECTION
            )


            sut.findCriteria(presenter, request)


            val expectedResponse = FindCriteriaService.ResponseModel(
                userId = researcherId,
                systematicStudyId = systematicStudyId,
                stage = StudyReviewStage.SELECTION,
                criteria = FindCriteriaService.FoundStudies(
                    included = emptyMap()
                )
            )


            verify(exactly = 1) {
                presenter.prepareSuccessView(expectedResponse)
            }
        }



        @Test
        fun `should return criteria with no studies when descriptions do not match`() {

            val inclusionCriterion = criteriaFactory.criteriaDto(
                description = "criterion1",
                type = "INCLUSION"
            )


            val protocolDto = protocolDtoFactory.protocolDto(
                systematicStudy = systematicStudyId,
                eligibilityCriteria = setOf(inclusionCriterion)
            )


            val studyReview = studyFactory.generateDto(
                systematicStudyId = systematicStudyId,
                selectionCriteria = setOf("differentCriterion")
            )


            every {
                protocolRepository.findById(systematicStudyId)
            } returns protocolDto


            every {
                studyReviewRepository.findAllFromReview(systematicStudyId)
            } returns listOf(studyReview)


            val request = FindCriteriaService.RequestModel(
                userId = researcherId,
                systematicStudyId = systematicStudyId,
                type = "INCLUSION",
                stage = StudyReviewStage.SELECTION
            )


            sut.findCriteria(presenter, request)


            val expectedResponse = FindCriteriaService.ResponseModel(
                userId = researcherId,
                systematicStudyId = systematicStudyId,
                stage = StudyReviewStage.SELECTION,
                criteria = FindCriteriaService.FoundStudies(
                    included = mapOf(
                        inclusionCriterion to emptyList()
                    )
                )
            )


            verify(exactly = 1) {
                presenter.prepareSuccessView(expectedResponse)
            }
        }
    }
}