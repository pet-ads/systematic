package br.all.application.report.find

import br.all.application.protocol.repository.ProtocolRepository
import br.all.application.report.find.presenter.FindCriteriaPresenter
import br.all.application.report.find.service.FindCriteriaServiceImpl
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.study.repository.StudyReviewRepository
import br.all.application.user.CredentialsService
import br.all.application.util.PreconditionCheckerMockingNew
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import java.util.UUID
import kotlin.test.Test
import br.all.application.study.util.TestDataFactory as StudyReviewFactory

class FindCriteriaServiceImplTest {

    @MockK(relaxUnitFun = true)
    private lateinit var protocolRepository: ProtocolRepository

    @MockK(relaxUnitFun = true)
    private lateinit var systematicStudyRepository: SystematicStudyRepository

    @MockK
    private lateinit var credentialsService: CredentialsService

    @MockK(relaxUnitFun = true)
    private lateinit var studyReviewRepository: StudyReviewRepository

    @MockK(relaxUnitFun = true)
    private lateinit var presenter: FindCriteriaPresenter

    @InjectMockKs
    private lateinit var sut: FindCriteriaServiceImpl

    private lateinit var precondition: PreconditionCheckerMockingNew
    private lateinit var factory: StudyReviewFactory

    private lateinit var researcherId: UUID
    private lateinit var systematicStudyId: UUID

    @BeforeEach
    fun setup() {
        factory = StudyReviewFactory()

        researcherId = factory.researcherId
        systematicStudyId = factory.systematicStudyId

        precondition = PreconditionCheckerMockingNew(
            presenter,
            credentialsService,
            systematicStudyRepository,
            researcherId,
            systematicStudyId
        )
        precondition.makeEverythingWork()
    }

    @Nested
    @DisplayName("When successfully finding criteria")
    inner class SuccessfullyFindingCriteria {
        @Test
        fun `should return criteria with associated studies`() {

        }

        @Test
        fun `should return empty results when no criteria match the type`() {

        }
    }

    @Nested
    @DisplayName("When failing to find criteria")
    inner class FailingFindingCriteria {
        @Test
        fun `should handle case when protocol is not found`() {

        }

        @Test
        fun `should handle failed preconditions`() {

        }
    }
}