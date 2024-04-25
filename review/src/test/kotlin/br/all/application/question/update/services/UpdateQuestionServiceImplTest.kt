package br.all.application.question.update.services

import br.all.application.question.create.CreateQuestionService
import br.all.application.question.repository.QuestionRepository
import br.all.application.question.update.presenter.UpdateQuestionPresenter
import br.all.application.question.util.TestDataFactory
import br.all.application.researcher.credentials.ResearcherCredentialsService
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.util.PreconditionCheckerMocking
import br.all.domain.services.UuidGeneratorService
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource

@Tag("UnitTest")
@Tag("ServiceTest")
@ExtendWith(MockKExtension::class)
class UpdateQuestionServiceImplTest {
    @MockK(relaxUnitFun = true)
    private lateinit var systematicRepository: SystematicStudyRepository

    @MockK(relaxUnitFun = true)
    private lateinit var repository: QuestionRepository

    @MockK
    private lateinit var credentialsService: ResearcherCredentialsService

    @MockK
    private lateinit var uuidGeneratorService: UuidGeneratorService

    @MockK(relaxed = true)
    private lateinit var presenter: UpdateQuestionPresenter

    @InjectMockKs
    private lateinit var sut: UpdateQuestionServiceImpl

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
    @DisplayName("When successfully updating a question")
    inner class WhenSuccessfullyUpdatingAQuestion {
        @BeforeEach
        fun setUp() = run { preconditionCheckerMocking.makeEverythingWork() }

        @ParameterizedTest
        @EnumSource(CreateQuestionService.QuestionType::class)
        fun `should successfully update a question code`(questionType: CreateQuestionService.QuestionType) {
            val dto = when (questionType) {
                CreateQuestionService.QuestionType.TEXTUAL -> factory.generateTextualDto()
                CreateQuestionService.QuestionType.NUMBERED_SCALE -> factory.generateNumberedScaleDto()
                CreateQuestionService.QuestionType.PICK_LIST -> factory.generatePickListDto()
                CreateQuestionService.QuestionType.LABELED_SCALE -> factory.generateLabeledScaleDto()
            }
            val updatedDto = dto.copy(code = "new code")
            val request = factory.updateQuestionRequestModel(updatedDto, questionType)
            val response = factory.updateQuestionResponseModel()

            every { repository.findById(factory.systematicStudy, factory.question) } returns dto
            sut.update(presenter, request)

            verify {
                repository.createOrUpdate(updatedDto)
                presenter.prepareSuccessView(response)
            }
        }

        @ParameterizedTest
        @EnumSource(CreateQuestionService.QuestionType::class)
        fun `should successfully update a question description`(questionType: CreateQuestionService.QuestionType) {
            val dto = when (questionType) {
                CreateQuestionService.QuestionType.TEXTUAL -> factory.generateTextualDto()
                CreateQuestionService.QuestionType.NUMBERED_SCALE -> factory.generateNumberedScaleDto()
                CreateQuestionService.QuestionType.PICK_LIST -> factory.generatePickListDto()
                CreateQuestionService.QuestionType.LABELED_SCALE -> factory.generateLabeledScaleDto()
            }
            val updatedDto = dto.copy(description = "new description")
            val request = factory.updateQuestionRequestModel(updatedDto, questionType)
            val response = factory.updateQuestionResponseModel()

            every { repository.findById(factory.systematicStudy, factory.question) } returns dto
            sut.update(presenter, request)

            verify {
                repository.createOrUpdate(updatedDto)
                presenter.prepareSuccessView(response)
            }
        }
    }
}