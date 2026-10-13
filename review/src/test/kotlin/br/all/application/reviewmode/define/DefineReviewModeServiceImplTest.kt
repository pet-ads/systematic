package br.all.application.reviewmode.define

import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.reviewmode.define.DefineReviewModeService.ModeType
import br.all.application.reviewmode.define.DefineReviewModeService.RequestModel
import br.all.application.reviewmode.repository.ReviewModeRepository
import br.all.application.user.CredentialsService
import br.all.application.util.PreconditionCheckerMockingNew
import br.all.domain.model.study.StudyReviewStage
import br.all.domain.shared.exception.EntityNotFoundException
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verifyOrder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.UUID

@Tag("ServiceTest")
@Tag("UnitTest")
@ExtendWith(MockKExtension::class)
class DefineReviewModeServiceImplTest {
    @MockK(relaxUnitFun = true)
    private lateinit var systematicStudyRepository: SystematicStudyRepository
    @MockK
    private lateinit var credentialsService: CredentialsService
    @MockK(relaxed = true)
    private lateinit var presenter: DefineReviewModePresenter
    @InjectMockKs
    private lateinit var sut: DefineReviewModeServiceImpl

    private val researcherId = UUID.randomUUID()
    private val systematicStudyId = UUID.randomUUID()
    private lateinit var preconditionCheckerMocking: PreconditionCheckerMockingNew

    @BeforeEach
    fun setUp() {
        preconditionCheckerMocking = PreconditionCheckerMockingNew(
            presenter,
            credentialsService,
            systematicStudyRepository,
            researcherId,
            systematicStudyId
        )
    }

    @Nested
    @Tag("InvalidClasses")
    @DisplayName("When preconditions fail")
    inner class WhenPreconditionsFail {
        @Test
        fun `should prepare fail view when the systematic study does not exist`() {
            val request = RequestModel(
                userId = researcherId,
                systematicStudyId = systematicStudyId,
                stage = StudyReviewStage.SELECTION,
                mode = ModeType.SPLIT
            )

            preconditionCheckerMocking.makeSystematicStudyNonexistent()
            sut.define(presenter, request)

            verifyOrder {
                presenter.prepareFailView(any<EntityNotFoundException>())
                presenter.isDone()
            }
        }
    }
}
