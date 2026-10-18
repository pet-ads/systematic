package br.all.application.reviewmode.define

import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.reviewmode.define.DefineReviewModeService.RequestModel
import br.all.application.reviewmode.define.DefineReviewModeService.SplitRequest
import br.all.application.reviewmode.define.DefineReviewModeService.ReplicationRequest
import br.all.application.reviewmode.define.DefineReviewModeService.ResponseModel
import br.all.application.reviewmode.repository.ReviewModeDto
import br.all.application.reviewmode.repository.ReviewModeRepository
import br.all.application.reviewmode.repository.toDto
import br.all.application.user.CredentialsService
import br.all.application.util.PreconditionCheckerMockingNew
import br.all.domain.model.study.StudyReviewStage
import br.all.domain.shared.exception.EntityNotFoundException
import br.all.domain.shared.exception.UnauthenticatedUserException
import br.all.domain.shared.exception.UnauthorizedUserException
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verifyOrder
import io.mockk.verify
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
    private lateinit var reviewModeRepository: ReviewModeRepository
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

    private val splitRequest = RequestModel(
        userId = researcherId,
        systematicStudyId = systematicStudyId,
        stage = StudyReviewStage.SELECTION,
        split = SplitRequest(reviewers = listOf(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID()), reviewPerStudy = 2)
    )

    private val replicationRequest = RequestModel(
        userId = researcherId,
        systematicStudyId = systematicStudyId,
        stage = StudyReviewStage.EXTRACTION,
        replication = ReplicationRequest(researcherId)
    )

    private val splitReviewModeDto: ReviewModeDto = ReviewModeDto(
        systematicStudyId,
        selection =  splitRequest.toReviewMode().toDto()
    )

    private val replicationReviewModeDto: ReviewModeDto = ReviewModeDto(
        systematicStudyId,
        extraction =  replicationRequest.toReviewMode().toDto()
    )

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
    @Tag("ValidClasses")
    @DisplayName("When defining a review mode")
    inner class WhenDefiningAReviewMode {
        @BeforeEach
        fun setUp() {
            val reviewModeDto = ReviewModeDto(systematicStudyId)
            preconditionCheckerMocking.makeEverythingWork()
            every { reviewModeRepository.findById(systematicStudyId) } returns reviewModeDto
        }

        @Test
        fun `should define a split review mode and prepare success view`() {
            val expected = ResponseModel(researcherId, systematicStudyId, StudyReviewStage.SELECTION)

            sut.define(presenter, splitRequest)

            verify(exactly = 1) { presenter.prepareSuccessView(expected) }
        }

        @Test
        fun `should define a replication review mode and prepare success view`() {
            val expected = ResponseModel(researcherId, systematicStudyId, StudyReviewStage.EXTRACTION)

            sut.define(presenter, replicationRequest)

            verify(exactly = 1) { presenter.prepareSuccessView(expected) }
        }

        @Test
        fun `should look for the review mode of the given systematic study`() {
            sut.define(presenter, splitRequest)

            verify(exactly = 1) { reviewModeRepository.findById(systematicStudyId) }
        }

        @Test
        fun `should save the review mode with the replication mode defined on the given stage`() {
            sut.define(presenter, replicationRequest)

            verify(exactly = 1) { reviewModeRepository.saveOrUpdate(replicationReviewModeDto) }
        }

        @Test
        fun `should save the review mode with the split mode defined on the given stage`() {
            sut.define(presenter, splitRequest)

            verify(exactly = 1) { reviewModeRepository.saveOrUpdate(splitReviewModeDto) }
        }
    }

    @Nested
    @Tag("InvalidClasses")
    @DisplayName("When preconditions fail")
    inner class WhenPreconditionsFail {
        @Test
        fun `should prepare fail view when the systematic study does not exist`() {
            preconditionCheckerMocking.makeSystematicStudyNonexistent()
            sut.define(presenter, splitRequest)

            verifyOrder {
                presenter.prepareFailView(any<EntityNotFoundException>())
                presenter.isDone()
            }
        }

        @Test
        fun `should not allow unauthenticated users to define review mode`() {
            preconditionCheckerMocking.makeUserUnauthenticated()
            sut.define(presenter, replicationRequest)

            verifyOrder {
                presenter.prepareFailView(any<UnauthenticatedUserException>())
                presenter.isDone()
            }
        }

        @Test
        fun `should not allow unauthorized users to define review mode`() {
            preconditionCheckerMocking.makeUserUnauthorized()
            sut.define(presenter, splitRequest)

            verifyOrder {
                presenter.prepareFailView(any<UnauthorizedUserException>())
                presenter.isDone()
            }
        }
    }
}
