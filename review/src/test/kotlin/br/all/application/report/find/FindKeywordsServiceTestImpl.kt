package br.all.application.report.find

import br.all.application.report.find.presenter.FindKeywordsPresenter
import br.all.application.report.find.service.FindKeywordsServiceImpl
import br.all.application.report.util.TestDataFactory
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.study.repository.StudyReviewRepository
import br.all.application.user.CredentialsService
import br.all.application.util.PreconditionCheckerMockingNew
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.extension.ExtendWith

@Tag("UnitTest")
@ExtendWith(MockKExtension::class)
class FindKeywordsServiceTestImpl {

    @MockK(relaxUnitFun = true)
    private lateinit var systematicStudyRepository: SystematicStudyRepository

    @MockK(relaxUnitFun = true)
    private lateinit var studyReviewRepository: StudyReviewRepository

    @MockK
    private lateinit var credentialsService: CredentialsService

    @MockK(relaxed = true)
    private lateinit var presenter: FindKeywordsPresenter

    @InjectMockKs
    private lateinit var sut: FindKeywordsServiceImpl

    private lateinit var preconditionCheckerMocking: PreconditionCheckerMockingNew
    private lateinit var factory: TestDataFactory

    @BeforeEach
    fun setup() {
        factory = TestDataFactory()
        preconditionCheckerMocking = PreconditionCheckerMockingNew(
            presenter, credentialsService, systematicStudyRepository, factory.researcher, factory.systematicStudy
        )
    }
}