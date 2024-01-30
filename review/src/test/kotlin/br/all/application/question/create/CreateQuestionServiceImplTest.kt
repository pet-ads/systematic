package br.all.application.question.create

import br.all.application.protocol.repository.ProtocolRepository
import br.all.application.question.create.CreateQuestionService.RequestModel
import br.all.application.question.create.CreateQuestionService.ResponseModel
import br.all.application.question.repository.QuestionDto
import br.all.application.question.repository.QuestionRepository
import br.all.application.question.shared.QuestionFactory
import br.all.application.researcher.credentials.ResearcherCredentialsService
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.shared.presenter.PreconditionChecker
import br.all.domain.model.protocol.ProtocolId
import br.all.domain.model.question.QuestionId
import br.all.domain.model.question.Textual
import br.all.domain.services.UuidGeneratorService
import io.mockk.*
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.*

@Tag("UnitTest")
@Tag("ServiceTest")
@ExtendWith(MockKExtension::class)
class CreateQuestionServiceImplTest {
    @MockK1
    private lateinit var factory: QuestionFactory
    @MockK(relaxUnitFun = true)
    private lateinit var repository: QuestionRepository
    @MockK(relaxUnitFun = true)
    private lateinit var systematicStudyRepository: SystematicStudyRepository
    @MockK(relaxUnitFun = true)
    private lateinit var protocolRepository: ProtocolRepository
    @MockK
    private lateinit var uuidGeneratorService: UuidGeneratorService
    @MockK
    private lateinit var credentialsService: ResearcherCredentialsService
    @MockK(relaxed = true)
    private lateinit var presenter: CreateQuestionPresenter
    private lateinit var sut: CreateQuestionServiceImpl

    @BeforeEach
    fun setUp() {
        val strategy = CreateExtractionQuestionStrategy(protocolRepository)
        mockkConstructor(PreconditionChecker::class)
        sut = CreateQuestionServiceImpl(
            factory,
            systematicStudyRepository,
            protocolRepository,
            strategy,
            uuidGeneratorService,
            credentialsService,
        )
    }

    @Test
    fun `should create a new TextualQuestion`() {
        val researcher = UUID.randomUUID()
        val systematicStudy = UUID.randomUUID()
        val questionId = UUID.randomUUID()
        val protocolId = UUID.randomUUID()
        val request = RequestModel(researcher, systematicStudy, protocolId, "Code", "Description")
        val textual = Textual(QuestionId(questionId), ProtocolId(protocolId), "Code", "Description")
        val dto = QuestionDto(questionId, protocolId, "Code", "Description", "Textual")

        every { factory.repository() } returns repository
        every { factory.create(questionId, request) } returns textual
        every {
            anyConstructed<PreconditionChecker>().prepareIfViolatesPreconditions(any(), any(), any(), any(), any())
        } just Runs
        every { uuidGeneratorService.next() } returns questionId
        every { factory.toDto(textual) } returns dto

        sut.create(presenter, request)

        verify {
            protocolRepository.create(any())
            repository.createOrUpdate(dto)
            presenter.prepareSuccessView(ResponseModel(researcher, systematicStudy, protocolId, questionId))
        }
    }
}
