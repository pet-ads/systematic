package br.all.application.report.export

import br.all.application.protocol.repository.ProtocolRepository
import br.all.application.protocol.util.TestDataFactory as ProtocolTestDataFactory
import br.all.application.study.util.TestDataFactory as StudyTestDataFactory
import br.all.application.question.repository.QuestionRepository
import br.all.application.report.export.presenter.ExportReviewPresenter
import br.all.application.report.export.service.ExportReviewService
import br.all.application.report.export.service.ExportReviewServiceImpl
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.study.repository.StudyReviewRepository
import br.all.application.user.CredentialsService
import br.all.application.util.PreconditionCheckerMockingNew
import br.all.domain.shared.exception.EntityNotFoundException
import br.all.domain.shared.exception.UnauthenticatedUserException
import br.all.domain.shared.exception.UnauthorizedUserException
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.slot
import io.mockk.verify
import io.mockk.verifyOrder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.UUID
import kotlin.test.assertEquals

@Tag("UnitTest")
@Tag("ServiceTest")
@ExtendWith(MockKExtension::class)
class ExportReviewServiceImplTest {

    @MockK(relaxUnitFun = true)
    private lateinit var systematicStudyRepository: SystematicStudyRepository

    @MockK(relaxUnitFun = true)
    private lateinit var protocolRepository: ProtocolRepository

    @MockK(relaxUnitFun = true)
    private lateinit var studyReviewRepository: StudyReviewRepository

    @MockK(relaxUnitFun = true)
    private lateinit var questionRepository: QuestionRepository

    @MockK
    private lateinit var credentialsService: CredentialsService

    @MockK
    private lateinit var reviewExporter: ReviewExporter

    @MockK(relaxed = true)
    private lateinit var presenter: ExportReviewPresenter

    private lateinit var sut: ExportReviewServiceImpl
    private lateinit var precondition: PreconditionCheckerMockingNew
    private lateinit var protocolFactory: ProtocolTestDataFactory
    private lateinit var studyFactory: StudyTestDataFactory

    private lateinit var researcherId: UUID
    private lateinit var systematicStudyId: UUID

    @BeforeEach
    fun setup() {
        protocolFactory = ProtocolTestDataFactory()
        studyFactory = StudyTestDataFactory()

        researcherId = protocolFactory.researcher
        systematicStudyId = protocolFactory.systematicStudy

        every { reviewExporter.format } returns "latex"
        every { reviewExporter.export(any()) } returns "\\documentclass{article}\\begin{document}Review Content\\end{document}"
        every { questionRepository.findAllBySystematicStudyId(any(), any()) } returns emptyList()

        sut = ExportReviewServiceImpl(
            credentialsService,
            systematicStudyRepository,
            protocolRepository,
            studyReviewRepository,
            questionRepository,
            mapOf("latex" to reviewExporter)
        )

        precondition = PreconditionCheckerMockingNew(
            presenter,
            credentialsService,
            systematicStudyRepository,
            researcherId,
            systematicStudyId
        )
    }

    @Nested
    @Tag("ValidClasses")
    @DisplayName("When being successful")
    inner class WhenBeingSuccessful {

        @Test
        fun `should export review in latex format`() {
            val protocolDto = protocolFactory.protocolDto()
            val output = "\\documentclass{article}\\begin{document}Review Content\\end{document}"

            precondition.makeEverythingWork()
            every { protocolRepository.findById(systematicStudyId) } returns protocolDto
            every { studyReviewRepository.findAllFromReview(systematicStudyId) } returns emptyList()
            every { reviewExporter.export(any()) } returns output

            val request = ExportReviewService.RequestModel(researcherId, systematicStudyId, "latex")
            sut.exportReview(presenter, request)

            val expectedResponse = ExportReviewService.ResponseModel(
                researcherId, systematicStudyId, "latex", output
            )
            verify(exactly = 1) { presenter.prepareSuccessView(expectedResponse) }
        }

        @Test
        fun `should return empty study lists when there are no studies`() {
            val protocolDto = protocolFactory.protocolDto()
            val slot = slot<ReviewExportData>()

            precondition.makeEverythingWork()
            every { protocolRepository.findById(systematicStudyId) } returns protocolDto
            every { studyReviewRepository.findAllFromReview(systematicStudyId) } returns emptyList()
            every { reviewExporter.export(capture(slot)) } returns "content"

            val request = ExportReviewService.RequestModel(researcherId, systematicStudyId, "latex")
            sut.exportReview(presenter, request)

            val reviewData = slot.captured
            assertEquals(0, reviewData.studiesExcludedInScreening.size)
            assertEquals(0, reviewData.studiesIncludedInScreening.size)
            assertEquals(0, reviewData.studiesExcludedInFullText.size)
            assertEquals(0, reviewData.includedStudies.size)
        }

        @Test
        fun `should correctly separate studies by stage`() {
            val protocolDto = protocolFactory.protocolDto()
            val slot = slot<ReviewExportData>()

            val studyExcludedInScreening = studyFactory.generateDto(
                systematicStudyId = systematicStudyId, studyReviewId = 1L,
                selectionStatus = "EXCLUDED", extractionStatus = "UNCLASSIFIED"
            )
            val studyIncludedInScreening = studyFactory.generateDto(
                systematicStudyId = systematicStudyId, studyReviewId = 2L,
                selectionStatus = "INCLUDED", extractionStatus = "UNCLASSIFIED"
            )
            val studyExcludedInFullText = studyFactory.generateDto(
                systematicStudyId = systematicStudyId, studyReviewId = 3L,
                selectionStatus = "INCLUDED", extractionStatus = "EXCLUDED"
            )
            val studyFullyIncluded = studyFactory.generateDto(
                systematicStudyId = systematicStudyId, studyReviewId = 4L,
                selectionStatus = "INCLUDED", extractionStatus = "INCLUDED"
            )

            precondition.makeEverythingWork()
            every { protocolRepository.findById(systematicStudyId) } returns protocolDto
            every { studyReviewRepository.findAllFromReview(systematicStudyId) } returns listOf(
                studyExcludedInScreening, studyIncludedInScreening, studyExcludedInFullText, studyFullyIncluded
            )
            every { reviewExporter.export(capture(slot)) } returns "content"

            val request = ExportReviewService.RequestModel(researcherId, systematicStudyId, "latex")
            sut.exportReview(presenter, request)

            val reviewData = slot.captured
            assertEquals(1, reviewData.studiesExcludedInScreening.size)
            assertEquals(3, reviewData.studiesIncludedInScreening.size)
            assertEquals(1, reviewData.studiesExcludedInFullText.size)
            assertEquals(1, reviewData.includedStudies.size)
        }

        @Test
        fun `should calculate funnel from all studies`() {
            val protocolDto = protocolFactory.protocolDto()
            val slot = slot<ReviewExportData>()

            val studies = listOf(
                studyFactory.generateDto(systematicStudyId = systematicStudyId, studyReviewId = 1L,
                    selectionStatus = "EXCLUDED", extractionStatus = "UNCLASSIFIED"),
                studyFactory.generateDto(systematicStudyId = systematicStudyId, studyReviewId = 2L,
                    selectionStatus = "INCLUDED", extractionStatus = "INCLUDED"),
            )

            precondition.makeEverythingWork()
            every { protocolRepository.findById(systematicStudyId) } returns protocolDto
            every { studyReviewRepository.findAllFromReview(systematicStudyId) } returns studies
            every { reviewExporter.export(capture(slot)) } returns "content"

            val request = ExportReviewService.RequestModel(researcherId, systematicStudyId, "latex")
            sut.exportReview(presenter, request)

            assertEquals(1, slot.captured.funnel.totalIncluded)
        }
    }

    @Nested
    @Tag("InvalidClasses")
    @DisplayName("When being unsuccessful")
    inner class WhenBeingUnsuccessful {

        @Test
        fun `should fail when format is unsupported`() {
            precondition.makeEverythingWork()

            val request = ExportReviewService.RequestModel(researcherId, systematicStudyId, "pdf")
            sut.exportReview(presenter, request)

            verify(exactly = 1) {
                presenter.prepareFailView(match<EntityNotFoundException> { it.message == "Invalid export format pdf" })
            }
        }

        @Test
        fun `should fail when systematic study does not exist`() {
            precondition.makeSystematicStudyNonexistent()

            val request = ExportReviewService.RequestModel(researcherId, systematicStudyId, "latex")
            sut.exportReview(presenter, request)

            verifyOrder {
                presenter.prepareFailView(any<EntityNotFoundException>())
                presenter.isDone()
            }
        }

        @Test
        fun `should fail when user is unauthenticated`() {
            precondition.makeUserUnauthenticated()

            val request = ExportReviewService.RequestModel(researcherId, systematicStudyId, "latex")
            sut.exportReview(presenter, request)

            verifyOrder {
                presenter.prepareFailView(any<UnauthenticatedUserException>())
                presenter.isDone()
            }
        }

        @Test
        fun `should fail when user is unauthorized`() {
            precondition.makeUserUnauthorized()

            val request = ExportReviewService.RequestModel(researcherId, systematicStudyId, "latex")
            sut.exportReview(presenter, request)

            verifyOrder {
                presenter.prepareFailView(any<UnauthorizedUserException>())
                presenter.isDone()
            }
        }
    }
}