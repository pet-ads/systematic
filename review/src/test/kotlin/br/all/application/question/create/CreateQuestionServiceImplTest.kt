package br.all.application.question.create

import br.all.application.question.create.CreateQuestionService.*
import br.all.application.question.repository.QuestionRepository
import br.all.application.question.util.TestDataFactory
import br.all.application.researcher.credentials.ResearcherCredentialsService
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.util.PreconditionCheckerMocking
import br.all.domain.services.UuidGeneratorService
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.runs
import io.mockk.verify
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import java.util.*

@Tag("UnitTest")
@Tag("ServiceTest")
@ExtendWith(MockKExtension::class)
class CreateQuestionServiceImplTest{
    @MockK(relaxUnitFun = true)
    private lateinit var systematicRepository: SystematicStudyRepository
    @MockK(relaxUnitFun = true)
    private lateinit var repository: QuestionRepository
    @MockK
    private lateinit var credentialsService: ResearcherCredentialsService
    @MockK
    private lateinit var uuidGeneratorService: UuidGeneratorService
    @MockK(relaxed = true)
    private lateinit var presenter: CreateQuestionPresenter
    @InjectMockKs
    private lateinit var sut: CreateQuestionServiceImpl

    private lateinit var factory: TestDataFactory
    private lateinit var preconditionCheckerMocking: PreconditionCheckerMocking

    @BeforeEach
    fun setUp() {
        factory = TestDataFactory()
        preconditionCheckerMocking = PreconditionCheckerMocking(
            presenter,
            credentialsService,
            systematicRepository,
            factory.researcher,
            factory.systematicStudy
        )
    }

    @Nested
    @Tag("ValidClasses")
    @DisplayName("When successfully creating a question")
    inner class WhenSuccessfullyCreatingAQuestion {
        @Test
        fun `should successfully create a textual question`() {
            val (researcher, systematicStudy, question) = factory
            val request = factory.createTextualRequestModel()
            val dto = factory.dtoFromRequest(request)
            val response = ResponseModel(researcher, systematicStudy, question)

            every { uuidGeneratorService.next() } returns question
            preconditionCheckerMocking.makeEverythingWork()

            sut.create(presenter, request)

            verify(exactly = 1){
                uuidGeneratorService.next()
                repository.createOrUpdate(dto)
                presenter.prepareSuccessView(response)
            }
        }
    }
}