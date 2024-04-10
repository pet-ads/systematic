package br.all.application.study.create

import br.all.application.researcher.credentials.ResearcherCredentialsService
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.study.create.CreateStudyReviewService.RequestModel
import br.all.application.study.repository.StudyReviewRepository
import br.all.application.study.repository.fromStudyRequestModel
import br.all.application.study.repository.toDto
import br.all.application.study.util.TestDataFactory
import br.all.application.util.PreconditionCheckerMocking
import br.all.domain.model.study.StudyReview
import br.all.domain.services.IdGeneratorService
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import java.util.*
@Tag("UnitTest")
@Tag("ServiceTest")
@ExtendWith(MockKExtension::class)
class CreateStudyReviewServiceImplTest {

    @MockK private lateinit var studyReviewRepository: StudyReviewRepository
    @MockK(relaxed = true) private lateinit var systematicStudyRepository: SystematicStudyRepository
    @MockK(relaxed = true) private lateinit var idGenerator: IdGeneratorService
    @MockK(relaxed = true) private lateinit var credentialService: ResearcherCredentialsService
    @MockK(relaxed = true) private lateinit var presenter: CreateStudyReviewPresenter

    private lateinit var sut: CreateStudyReviewServiceImpl

    private lateinit var factory: TestDataFactory
    private lateinit var preconditionCheckerMocking: PreconditionCheckerMocking

    @BeforeEach
    fun setUp() {
        factory = TestDataFactory()
        preconditionCheckerMocking = PreconditionCheckerMocking(
            presenter,
            credentialService,
            systematicStudyRepository,
            factory.researcherId,
            factory.systematicStudyId
        )
        sut = CreateStudyReviewServiceImpl(
            systematicStudyRepository,
            studyReviewRepository,
            credentialService,
            idGenerator
        )
    }

    @Nested
    @Tag("ValidClasses")
    @DisplayName("When successfully creating a Study Review")
    inner class WhenSuccessfullyCreatingAStudyReview {
        @Test
        fun `should successfully create a Study Review`() {
            val (_, studyReviewId) = factory
            val request = factory.createRequestModel()


            preconditionCheckerMocking.makeEverythingWork()
            every { idGenerator.next() } returns studyReviewId

            sut.createFromStudy(presenter, request)

            verify(exactly = 1) {
                val studyId = idGenerator.next()
                val studyReview = StudyReview.fromStudyRequestModel(studyId, request)

                studyReviewRepository.saveOrUpdate(studyReview.toDto())
                presenter.prepareSuccessView(
                    CreateStudyReviewService.ResponseModel(
                        request.researcherId,
                        request.systematicStudyId,
                        studyId
                    )
                )
            }
        }
    }

}