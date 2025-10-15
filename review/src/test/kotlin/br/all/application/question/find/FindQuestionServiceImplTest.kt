package br.all.application.question.find

import br.all.application.question.create.CreateQuestionService.*
import br.all.application.question.repository.QuestionRepository
import br.all.application.question.util.TestDataFactory
import br.all.application.review.repository.SystematicStudyRepository
import br.all.domain.shared.exception.UnauthenticatedUserException
import br.all.domain.shared.exception.UnauthorizedUserException
import br.all.application.user.CredentialsService
import br.all.application.util.PreconditionCheckerMockingNew
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
class FindQuestionServiceImplTest {
    @MockK(relaxUnitFun = true)
    private lateinit var systematicRepository: SystematicStudyRepository

    @MockK(relaxUnitFun = true)
    private lateinit var repository: QuestionRepository

    @MockK
    private lateinit var credentialsService: CredentialsService

    @MockK(relaxed = true)
    private lateinit var presenter: FindQuestionPresenter

    @InjectMockKs
    private lateinit var sut: FindQuestionServiceImpl

    private lateinit var factory: TestDataFactory
    private lateinit var preconditionCheckerMocking: PreconditionCheckerMockingNew

    @BeforeEach
    fun setUp() {
        factory = TestDataFactory()
        preconditionCheckerMocking = PreconditionCheckerMockingNew(
            presenter, credentialsService, systematicRepository, factory.researcher, factory.systematicStudy
        )
    }

    @Nested
    @Tag("ValidClasses")
    @DisplayName("When successfully finding a question")
    inner class WhenSuccessfullyFindingAQuestion {
        @ParameterizedTest
        @EnumSource(QuestionType::class)
        fun `Should correctly find a question and prepare success view`(questionType: QuestionType) {
            val (_, systematicStudy, question) = factory
            val request = factory.findOneQuestionRequestModel()
            val response = when (questionType) {
                QuestionType.TEXTUAL -> factory.findOneTextualResponseModel()
                QuestionType.NUMBERED_SCALE -> factory.findOneNumberedScaleResponseModel()
                QuestionType.PICK_LIST -> factory.findOnePickListResponseModel()
                QuestionType.PICK_MANY -> factory.findOnePickManyResponseModel()
                QuestionType.LABELED_SCALE -> factory.findOneLabeledScaleResponseModel()
            }

            preconditionCheckerMocking.makeEverythingWork()
            every { repository.findById(systematicStudy, question) } returns response.content

            sut.findOne(presenter, request)
            verify { presenter.prepareSuccessView(response) }
        }
    }

    @Nested
    @Tag("InvalidClasses")
    @DisplayName("When being unable to find a question")
    inner class WhenBeingUnableToFindAQuestion {
        @Test
        fun `should prepare a fail view when trying to find a nonexistent question`() {
            val request = factory.findOneQuestionRequestModel()

            preconditionCheckerMocking.testForNonexistentQuestion(presenter, request, request.questionId, repository) { _, _ ->
                sut.findOne(presenter, request)
            }
        }

        @Test
        fun `should prepare a fail view if the researcher is not a collaborator`() {
            val request = factory.findOneQuestionRequestModel()
            preconditionCheckerMocking.makeUserUnauthorized()

            sut.findOne(presenter, request)
            verifyOrder {
                presenter.prepareFailView(any<UnauthorizedUserException>())
                presenter.isDone()
            }
        }

        @Test
        fun `should a unauthenticated researcher be unable to find any question`() {
            val request = factory.findOneQuestionRequestModel()

            preconditionCheckerMocking.makeUserUnauthenticated()

            sut.findOne(presenter, request)
            verifyOrder {
                presenter.prepareFailView(any<UnauthenticatedUserException>())
                presenter.isDone()
            }
        }

        @Test
        fun `should a unauthorized researcher be unable to find any question`() {
            val request = factory.findOneQuestionRequestModel()

            preconditionCheckerMocking.makeUserUnauthorized()

            sut.findOne(presenter, request)
            verifyOrder {
                presenter.prepareFailView(any<UnauthorizedUserException>())
                presenter.isDone()
            }
        }
    }
}