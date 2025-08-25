package br.all.application.question.update.services

import br.all.application.question.create.CreateQuestionService.*
import br.all.application.question.create.CreateQuestionService.QuestionType.*
import br.all.application.question.repository.QuestionRepository
import br.all.application.question.update.presenter.UpdateQuestionPresenter
import br.all.application.question.util.TestDataFactory
import br.all.application.user.credentials.ResearcherCredentialsService
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.search.repository.SearchSessionRepository
import br.all.application.user.CredentialsService
import br.all.application.util.PreconditionCheckerMocking
import br.all.application.util.PreconditionCheckerMockingNew
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
    private lateinit var credentialsService: CredentialsService

    @MockK
    private lateinit var uuidGeneratorService: UuidGeneratorService

    @MockK(relaxed = true)
    private lateinit var presenter: UpdateQuestionPresenter

    @InjectMockKs
    private lateinit var sut: UpdateQuestionServiceImpl

    private lateinit var factory: TestDataFactory
    private lateinit var preconditionCheckerMocking: PreconditionCheckerMockingNew

    @BeforeEach
    fun setUp() {
        factory = TestDataFactory()
        preconditionCheckerMocking = PreconditionCheckerMockingNew(
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
        @EnumSource(QuestionType::class)
        fun `should successfully update a question code`(questionType: QuestionType) {
            val dto = when (questionType) {
                TEXTUAL -> factory.generateTextualDto()
                NUMBERED_SCALE -> factory.generateNumberedScaleDto()
                PICK_LIST -> factory.generatePickListDto()
                PICK_MANY -> factory.generatePickManyDto()
                LABELED_SCALE -> factory.generateLabeledScaleDto()
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
        @EnumSource(QuestionType::class)
        fun `should successfully update a question description`(questionType: QuestionType) {
            val dto = when (questionType) {
                TEXTUAL -> factory.generateTextualDto()
                NUMBERED_SCALE -> factory.generateNumberedScaleDto()
                PICK_LIST -> factory.generatePickListDto()
                PICK_MANY -> factory.generatePickManyDto()
                LABELED_SCALE -> factory.generateLabeledScaleDto()
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

        @Test
        fun `should successfully update a picklist question list of options`() {
            val dto = factory.generatePickListDto()
            val updatedDto = dto.copy(options = listOf("new option 1", " new option 2"))
            val request = factory.updateQuestionRequestModel(updatedDto, PICK_LIST)
            val response = factory.updateQuestionResponseModel()
            every { repository.findById(factory.systematicStudy, factory.question) } returns dto
            sut.update(presenter, request)

            verify {
                repository.createOrUpdate(updatedDto)
                presenter.prepareSuccessView(response)
            }
        }

        @Test
        fun `should successfully update a labeled-scale question map of labels`() {
            val dto = factory.generateLabeledScaleDto()
            val updatedDto = dto.copy(scales = mapOf("new label 1" to 1, "new label 1" to 2))
            val request = factory.updateQuestionRequestModel(updatedDto, LABELED_SCALE)
            val response = factory.updateQuestionResponseModel()
            every { repository.findById(factory.systematicStudy, factory.question) } returns dto
            sut.update(presenter, request)
            verify {
                repository.createOrUpdate(updatedDto)
                presenter.prepareSuccessView(response)
            }
        }

        @Test
        fun `should successfully update a numbered-scale question higher boundary if it is bigger than lower boundary value`() {
            val dto = factory.generateNumberedScaleDto()
            val updatedDto = dto.copy(higher = 11)
            val request = factory.updateQuestionRequestModel(updatedDto, NUMBERED_SCALE)
            val response = factory.updateQuestionResponseModel()
            every { repository.findById(factory.systematicStudy, factory.question) } returns dto
            sut.update(presenter, request)
            verify {
                repository.createOrUpdate(updatedDto)
                presenter.prepareSuccessView(response)
            }
        }

        @Test
        fun `should successfully update a numbered-scale question lower boundary if it is smaller than bigger boundary value`() {
            val dto = factory.generateNumberedScaleDto()
            val updatedDto = dto.copy(lower = 2)
            val request = factory.updateQuestionRequestModel(updatedDto, NUMBERED_SCALE)
            val response = factory.updateQuestionResponseModel()
            every { repository.findById(factory.systematicStudy, factory.question) } returns dto
            sut.update(presenter, request)
            verify {
                repository.createOrUpdate(updatedDto)
                presenter.prepareSuccessView(response)
            }
        }
    }

    @Nested
    @Tag("InvalidClasses")
    @DisplayName("When not able to update a question")
    inner class WhenNotAbleToUpdateAQuestion{

    }
}