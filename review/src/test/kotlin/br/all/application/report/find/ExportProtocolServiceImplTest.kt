package br.all.application.report.find

import br.all.application.protocol.repository.ProtocolRepository
import br.all.application.report.find.presenter.ExportProtocolPresenter
import br.all.application.report.find.service.ExportProtocolService
import br.all.application.report.find.service.ExportProtocolServiceImpl
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.protocol.util.TestDataFactory as ProtocolDtoFactory
import br.all.application.user.CredentialsService
import br.all.application.util.PreconditionCheckerMockingNew
import br.all.domain.services.FormatterFactoryService
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

    @MockK(relaxUnitFun = true)
    private lateinit var presenter: ExportProtocolPresenter

    @InjectMockKs
    private lateinit var sut: ExportProtocolServiceImpl

    private lateinit var precondition: PreconditionCheckerMockingNew
    private lateinit var factory: ProtocolDtoFactory

    @BeforeEach
    fun setup() {
        factory = ProtocolDtoFactory()
        precondition = PreconditionCheckerMockingNew(
            presenter,
            credentialsService,
            systematicStudyRepository,
            factory.researcher,
            factory.systematicStudy
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
            val formattedProtocol = "id,goal,justification\n1,Test Goal,Test Justification"

            every { protocolRepository.findById(factory.systematicStudy) } returns protocolDto
            every { formatterFactoryService.format(type, any() ) } returns formattedProtocol

            val request = ExportProtocolService.RequestModel(
                factory.researcher,
                factory.systematicStudy,
                type
            )

            sut.exportProtocol(presenter, request)

            val expectedResponse = ExportProtocolService.ResponseModel(
                factory.researcher,
                factory.systematicStudy,
                type,
                formattedProtocol
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
            val formattedProtocol = "\\documentclass{article}\n\\begin{document}\nProtocol Content\n\\end{document}"

            every { protocolRepository.findById(factory.systematicStudy) } returns protocolDto
            every { formatterFactoryService.format(type, any() ) } returns formattedProtocol

            val request = ExportProtocolService.RequestModel(
                factory.researcher,
                factory.systematicStudy,
                type
            )

            sut.exportProtocol(presenter, request)

            val expectedResponse = ExportProtocolService.ResponseModel(
                factory.researcher,
                factory.systematicStudy,
                type,
                formattedProtocol
            )

            verify(exactly = 1) {
                presenter.prepareSuccessView(expectedResponse)
            }
        }
    }
}