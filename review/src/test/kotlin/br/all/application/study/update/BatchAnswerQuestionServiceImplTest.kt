package br.all.application.study.update

import br.all.application.question.repository.QuestionRepository
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.study.repository.StudyReviewRepository
import br.all.application.study.update.implementation.BatchAnswerQuestionServiceImpl
import br.all.application.study.update.interfaces.BatchAnswerQuestionPresenter
import br.all.application.study.util.TestDataFactory
import br.all.application.user.CredentialsService
import br.all.application.util.PreconditionCheckerMockingNew
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.extension.ExtendWith
import java.util.UUID

@Tag("UnitTest")
@Tag("ServiceTest")
@ExtendWith(MockKExtension::class)
class BatchAnswerQuestionServiceImplTest {

    @MockK(relaxed = true)
    private lateinit var studyReviewRepository: StudyReviewRepository

    @MockK
    private lateinit var systematicStudyRepository: SystematicStudyRepository

    @MockK
    private lateinit var questionRepository: QuestionRepository

    @MockK
    private lateinit var credentialsService: CredentialsService

    @MockK(relaxed = true)
    private lateinit var presenter: BatchAnswerQuestionPresenter

    private lateinit var sut: BatchAnswerQuestionServiceImpl
    private lateinit var factory: TestDataFactory
    private lateinit var preconditionCheckerMocking: PreconditionCheckerMockingNew
    private lateinit var questionId: UUID

    @BeforeEach
    fun setUp() {
        factory = TestDataFactory()
        preconditionCheckerMocking = PreconditionCheckerMockingNew(
            presenter,
            credentialsService,
            systematicStudyRepository,
            factory.researcherId,
            factory.systematicStudyId
        )
        sut = BatchAnswerQuestionServiceImpl(
            studyReviewRepository,
            questionRepository,
            systematicStudyRepository,
            credentialsService
        )
        questionId = UUID.randomUUID()
    }




}