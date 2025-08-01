package br.all.application.report.find

import br.all.application.collaboration.repository.CollaborationRepository
import br.all.application.protocol.repository.ProtocolRepository
import br.all.application.report.find.presenter.ExportProtocolPresenter
import br.all.application.report.find.service.ExportProtocolService
import br.all.application.report.find.service.ExportProtocolServiceImpl
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.shared.exceptions.EntityNotFoundException
import br.all.application.protocol.util.TestDataFactory as ProtocolDtoFactory
import br.all.application.user.CredentialsService
import br.all.application.util.PreconditionCheckerMockingNew
import br.all.domain.services.FormatterFactoryService
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import java.util.UUID
import kotlin.test.Test

@Tag("UnitTest")
@Tag("ServiceTest")
@ExtendWith(MockKExtension::class)
class ExportProtocolServiceImplTest {

    @MockK(relaxUnitFun = true)
    private lateinit var systematicStudyRepository: SystematicStudyRepository

    @MockK(relaxUnitFun = true)
    private lateinit var protocolRepository: ProtocolRepository

    @MockK
    private lateinit var credentialsService: CredentialsService

    @MockK
    private lateinit var formatterFactoryService: FormatterFactoryService

    @MockK
    private lateinit var collaborationRepository: CollaborationRepository

    @MockK(relaxUnitFun = true)
    private lateinit var presenter: ExportProtocolPresenter

    @InjectMockKs
    private lateinit var sut: ExportProtocolServiceImpl

    private lateinit var precondition: PreconditionCheckerMockingNew
    private lateinit var factory: ProtocolDtoFactory

    private lateinit var researcherId: UUID
    private lateinit var systematicStudyId: UUID

    @BeforeEach
    fun setup() {
        factory = ProtocolDtoFactory()

        researcherId = factory.researcher
        systematicStudyId = factory.systematicStudy

        precondition = PreconditionCheckerMockingNew(
            presenter,
            credentialsService,
            systematicStudyRepository,
            collaborationRepository,
            researcherId,
            systematicStudyId,
            factory.collaboration
        )
        precondition.makeEverythingWork()
    }

    @Nested
    @DisplayName("When exporting in CSV format")
    inner class ExportInCSV {
        @Test
        fun `should properly return protocol formatted to CSV`() {
            val protocolDto = factory.protocolDto()
            val type = "csv"
            val output = "id,goal,justification\n1,Test Goal,Test Justification"

            every { protocolRepository.findById(systematicStudyId) } returns protocolDto
            every { formatterFactoryService.format(type, any() ) } returns output

            val request = ExportProtocolService.RequestModel(
                researcherId,
                systematicStudyId,
                type
            )

            sut.exportProtocol(presenter, request)

            val expectedResponse = ExportProtocolService.ResponseModel(
                researcherId,
                systematicStudyId,
                type,
                output
            )

            verify(exactly = 1) {
                presenter.prepareSuccessView(expectedResponse)
            }
        }
    }

    @Nested
    @DisplayName("When exporting in Latex format")
    inner class ExportInLatex {
        @Test
        fun `should properly return protocol formatted to Latex`() {
            val protocolDto = factory.protocolDto()
            val type = "Latex"
            val output = "\\documentclass{article}\n\\begin{document}\nProtocol Content\n\\end{document}"

            every { protocolRepository.findById(systematicStudyId) } returns protocolDto
            every { formatterFactoryService.format(type, any() ) } returns output

            val request = ExportProtocolService.RequestModel(
                researcherId,
                systematicStudyId,
                type
            )

            sut.exportProtocol(presenter, request)

            val expectedResponse = ExportProtocolService.ResponseModel(
                researcherId,
                systematicStudyId,
                type,
                output
            )

            verify(exactly = 1) {
                presenter.prepareSuccessView(expectedResponse)
            }
        }
    }

    @Nested
    @DisplayName("When exporting in an unsupported format")
    inner class ExportInUnsupportedFormat {
        @Test
        fun `should return an error message when using an unsupported format`() {
            val protocolDto = factory.protocolDto()
            val type = "pdf"
            val output = null

            every { protocolRepository.findById(systematicStudyId) } returns protocolDto
            every { formatterFactoryService.format(type, any() ) } returns output

            val request = ExportProtocolService.RequestModel(
                researcherId,
                systematicStudyId,
                type
            )

            sut.exportProtocol(presenter, request)

            val message = "Invalid export format ${request.format}"
            verify(exactly = 1) {
                presenter.prepareFailView(match<EntityNotFoundException> { it.message == message })
            }
        }
    }
}