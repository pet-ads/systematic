package br.all.application.report.find

import br.all.application.protocol.repository.ProtocolRepository
import br.all.application.report.find.presenter.FindSourcePresenter
import br.all.application.report.find.service.FindSourceService
import br.all.application.report.find.service.FindSourceServiceImpl
import br.all.application.review.repository.SystematicStudyRepository
import br.all.domain.shared.exception.EntityNotFoundException
import br.all.application.study.repository.StudyReviewRepository
import br.all.application.user.CredentialsService
import br.all.application.util.PreconditionCheckerMockingNew
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
import br.all.application.protocol.util.TestDataFactory as ProtocolDtoFactory

@Tag("UnitTest")
@Tag("ServiceTest")
@ExtendWith(MockKExtension::class)
class FindSourceServiceImplTest {

    @MockK(relaxUnitFun = true)
    private lateinit var protocolRepository: ProtocolRepository

    @MockK(relaxUnitFun = true)
    private lateinit var systematicStudyRepository: SystematicStudyRepository

    @MockK(relaxUnitFun = true)
    private lateinit var studyReviewRepository: StudyReviewRepository

    @MockK
    private lateinit var credentialsService: CredentialsService

    @MockK(relaxUnitFun = true)
    private lateinit var presenter: FindSourcePresenter

    @InjectMockKs
    private lateinit var sut: FindSourceServiceImpl

    private lateinit var precondition: PreconditionCheckerMockingNew
    private lateinit var studyFactory: StudyReviewFactory
    private lateinit var protocolDtoFactory: ProtocolDtoFactory

    private lateinit var researcherId: UUID
    private lateinit var systematicStudyId: UUID

    @BeforeEach
    fun setup() {
        studyFactory = StudyReviewFactory()
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
    @DisplayName("When successfully finding studies by source")
    inner class SuccessfullyFindingBySource {
        @Test
        fun `should return studies grouped by selection status`() {
            val source = "Scopus"

            val protocolDto = protocolDtoFactory.protocolDto(
                systematicStudy = systematicStudyId,
                informationSources = setOf("Scopus")
            )

            val includedStudy = studyFactory.generateDto(
                systematicStudyId = systematicStudyId,
                selectionStatus = "INCLUDED",
                sources = setOf("Scopus")
            )
            val excludedStudy = studyFactory.generateDto(
                systematicStudyId = systematicStudyId,
                selectionStatus = "EXCLUDED",
                sources = setOf("Scopus")
            )
            val duplicatedStudy = studyFactory.generateDto(
                systematicStudyId = systematicStudyId,
                selectionStatus = "DUPLICATED",
                sources = setOf("Scopus")
            )

            every { protocolRepository.findById(systematicStudyId) } returns protocolDto
            every { studyReviewRepository.findAllBySource(systematicStudyId, source) } returns
                listOf(includedStudy, excludedStudy, duplicatedStudy)

            val request = FindSourceService.RequestModel(
                researcherId,
                systematicStudyId,
                source
            )

            sut.findSource(presenter, request)

            val expectedResponse = FindSourceService.ResponseModel(
                userId = researcherId,
                systematicStudyId = systematicStudyId,
                source = source,
                included = listOf(includedStudy.studyReviewId),
                excluded = listOf(excludedStudy.studyReviewId),
                duplicated = listOf(duplicatedStudy.studyReviewId),
                totalOfStudies = 3
            )

            verify(exactly = 1) {
                presenter.prepareSuccessView(expectedResponse)
            }
        }
    }

    @Nested
    @DisplayName("When failing finding studies by source")
    inner class FailingFindingBySource {
        @Test
        fun `should fail when protocol is not found`() {
            val source = "Scopus"

            every { protocolRepository.findById(systematicStudyId) } returns null

            val request = FindSourceService.RequestModel(
                researcherId,
                systematicStudyId,
                source
            )

            sut.findSource(presenter, request)

            val message = "Unable to find protocol at '${systematicStudyId}'."
            verify(exactly = 1) {
                presenter.prepareFailView(match<EntityNotFoundException> { it.message == message })
            }
        }

        @Test
        fun `should fail when source is not in protocol`() {
            val source = "IEEE"

            val protocolDto = protocolDtoFactory.protocolDto(
                systematicStudy = systematicStudyId,
                informationSources = setOf("Scopus", "ACM")
            )

            every { protocolRepository.findById(systematicStudyId) } returns protocolDto

            val request = FindSourceService.RequestModel(
                researcherId,
                systematicStudyId,
                source
            )

            sut.findSource(presenter, request)

            val message = "Search source does not exist in protocol '${systematicStudyId}'."
            verify(exactly = 1) {
                presenter.prepareFailView(match<EntityNotFoundException> { it.message == message })
            }
        }
    }
}