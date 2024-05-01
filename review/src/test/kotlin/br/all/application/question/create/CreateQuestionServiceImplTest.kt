package br.all.application.question.create

import br.all.application.question.create.CreateQuestionService.QuestionType
import br.all.application.question.create.CreateQuestionService.QuestionType.*
import br.all.application.question.create.CreateQuestionService.ResponseModel
import br.all.application.question.repository.QuestionRepository
import br.all.application.question.util.TestDataFactory
import br.all.application.user.credentials.ResearcherCredentialsService
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.shared.exceptions.UnauthenticatedUserException
import br.all.application.shared.exceptions.UnauthorizedUserException
import br.all.application.util.PreconditionCheckerMocking
import br.all.domain.services.UuidGeneratorService
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import io.mockk.verifyOrder
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource

@Tag("UnitTest")
@Tag("ServiceTest")
@ExtendWith(MockKExtension::class)
class CreateQuestionServiceImplTest {
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
        @ParameterizedTest
        @EnumSource(QuestionType::class)
        fun `should successfully create a question`(questionType: QuestionType) {
            val request = when (questionType) {
                TEXTUAL -> factory.createTextualRequestModel()
                NUMBERED_SCALE -> factory.createNumberedScaleRequestModel()
                PICK_LIST -> factory.createPickListRequestModel()
                LABELED_SCALE -> factory.createLabeledScaleRequestModel()
            }

            val (researcher, systematicStudy, question) = factory
            val dto = factory.dtoFromRequest(request)
            val response = ResponseModel(researcher, systematicStudy, question)

            every { uuidGeneratorService.next() } returns question
            preconditionCheckerMocking.makeEverythingWork()

            sut.create(presenter, request)

            verify(exactly = 1) {
                uuidGeneratorService.next()
                repository.createOrUpdate(dto)
                presenter.prepareSuccessView(response)
            }
        }
    }

    @Nested
    @Tag("InvalidClasses")
    @DisplayName("When unable to create a question")
    inner class WhenUnableToCreateAQuestion {
        @Test
        fun `should not the researcher be allowed to create a new question when unauthenticated`() {
            val request = factory.createTextualRequestModel()
            preconditionCheckerMocking.makeResearcherUnauthenticated()
            sut.create(presenter, request)

            verifyOrder {
                presenter.prepareFailView(any<UnauthenticatedUserException>())
                presenter.isDone()
            }
        }

        @Test
        fun `should not the researcher be allowed to create a new question when unauthorized`() {
            val request = factory.createTextualRequestModel()
            preconditionCheckerMocking.makeResearcherUnauthorized()
            sut.create(presenter, request)

            verifyOrder {
                presenter.prepareFailView(any<UnauthorizedUserException>())
                presenter.isDone()
            }
        }

        @Test
        fun `should not be able to create picklist question if options is empty`() {
            val request = factory.createPickListRequestModel(options = emptyList())
            val (_, _, question) = factory

            every { uuidGeneratorService.next() } returns question
            preconditionCheckerMocking.makeEverythingWork()

            sut.create(presenter, request)
            verify {
                presenter.isDone()
                presenter.prepareFailView(any<IllegalArgumentException>())
            }
        }

        @Test
        fun `should not be able to create labeledScale question if scales is empty`() {
            val request = factory.createLabeledScaleRequestModel(scales = emptyMap<String, Int>())
            val (_, _, question) = factory

            every { uuidGeneratorService.next() } returns question
            preconditionCheckerMocking.makeEverythingWork()

            sut.create(presenter, request)
            verifyOrder {
                presenter.isDone()
                presenter.prepareFailView(any<IllegalArgumentException>())
            }
        }

        @Test
        fun `should not be able to create numbered-scale question if one of the boundaries is null`() {
            val request = factory.createNumberedScaleRequestModel(higher = null)
            val (_, _, question) = factory

            every { uuidGeneratorService.next() } returns question
            preconditionCheckerMocking.makeEverythingWork()

            sut.create(presenter, request)
            verifyOrder {
                presenter.isDone()
                presenter.prepareFailView(any<IllegalArgumentException>())
            }
        }
    }
}