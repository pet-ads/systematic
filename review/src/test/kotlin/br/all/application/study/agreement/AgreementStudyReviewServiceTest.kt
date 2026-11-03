package br.all.application.study.agreement

import br.all.application.shared.service.AuthorizationService
import br.all.application.study.update.implementation.UpdateStudyReviewExtractionService
import br.all.application.study.update.implementation.UpdateStudyReviewPriorityService
import br.all.application.study.update.implementation.UpdateStudyReviewSelectionService
import br.all.application.study.update.interfaces.BatchAnswerQuestionPresenter
import br.all.application.study.update.interfaces.BatchAnswerQuestionService
import br.all.application.study.update.interfaces.RemoveCriteriaPresenter
import br.all.application.study.update.interfaces.RemoveCriteriaService
import br.all.application.study.update.interfaces.UpdateStudyReviewStatusPresenter
import br.all.application.study.update.interfaces.UpdateStudyReviewStatusService
import br.all.domain.model.study.StudyReviewStage
import br.all.domain.shared.user.Role
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.*

@Tag("UnitTest")
@Tag("ServiceTest")
@ExtendWith(MockKExtension::class)
class AgreementStudyReviewServiceTest {

    @MockK private lateinit var authorizationService: AuthorizationService
    @MockK private lateinit var authorizationContext: AuthorizationService.AuthorizationContext
    @MockK private lateinit var selectionService: UpdateStudyReviewSelectionService
    @MockK private lateinit var extractionService: UpdateStudyReviewExtractionService
    @MockK private lateinit var priorityService: UpdateStudyReviewPriorityService
    @MockK private lateinit var batchAnswerQuestionService: BatchAnswerQuestionService
    @MockK private lateinit var removeCriteriaService: RemoveCriteriaService
    @MockK(relaxed = true) private lateinit var statusPresenter: UpdateStudyReviewStatusPresenter
    @MockK(relaxed = true) private lateinit var batchPresenter: BatchAnswerQuestionPresenter
    @MockK(relaxed = true) private lateinit var removePresenter: RemoveCriteriaPresenter

    private lateinit var sut: AgreementStudyReviewService

    private val userId = UUID.randomUUID()
    private val systematicStudyId = UUID.randomUUID()
    private val forgedReviewerId = UUID.randomUUID()

    private val statusRequest = UpdateStudyReviewStatusService.RequestModel(
        userId, systematicStudyId, listOf(1L), "INCLUDED", setOf("c1"),
        reviewerId = forgedReviewerId, stage = StudyReviewStage.SELECTION
    )
    private val batchRequest = BatchAnswerQuestionService.RequestModel(
        userId, systematicStudyId, 1L, emptyList(), reviewerId = forgedReviewerId
    )
    private val removeRequest = RemoveCriteriaService.RequestModel(
        userId, systematicStudyId, 1L, listOf("c1"), StudyReviewStage.SELECTION, reviewerId = forgedReviewerId
    )

    @BeforeEach
    fun setUp() {
        sut = AgreementStudyReviewService(
            authorizationService,
            selectionService,
            extractionService,
            priorityService,
            batchAnswerQuestionService,
            removeCriteriaService
        )
    }

    private fun allow() {
        every {
            authorizationService.authorize(any(), userId, systematicStudyId, setOf(Role.OWNER))
        } returns authorizationContext
    }

    private fun deny() {
        every {
            authorizationService.authorize(any(), userId, systematicStudyId, setOf(Role.OWNER))
        } returns null
    }

    @Test
    fun `should resolve the selection status on the original study when the user is allowed`() {
        allow()
        every { selectionService.changeStatus(statusPresenter, statusRequest.copy(reviewerId = null)) } just Runs

        sut.changeSelectionStatus(statusPresenter, statusRequest)

        verify(exactly = 1) { selectionService.changeStatus(statusPresenter, statusRequest.copy(reviewerId = null)) }
    }

    @Test
    fun `should not resolve the selection status when the user is not allowed`() {
        deny()

        sut.changeSelectionStatus(statusPresenter, statusRequest)

        verify(exactly = 0) { selectionService.changeStatus(any(), any()) }
    }

    @Test
    fun `should resolve the extraction status on the original study when the user is allowed`() {
        allow()
        every { extractionService.changeStatus(statusPresenter, statusRequest.copy(reviewerId = null)) } just Runs

        sut.changeExtractionStatus(statusPresenter, statusRequest)

        verify(exactly = 1) { extractionService.changeStatus(statusPresenter, statusRequest.copy(reviewerId = null)) }
    }

    @Test
    fun `should not resolve the extraction status when the user is not allowed`() {
        deny()

        sut.changeExtractionStatus(statusPresenter, statusRequest)

        verify(exactly = 0) { extractionService.changeStatus(any(), any()) }
    }

    @Test
    fun `should change the reading priority of the original study when the user is allowed`() {
        allow()
        every { priorityService.changeStatus(statusPresenter, statusRequest.copy(reviewerId = null)) } just Runs

        sut.changeReadingPriority(statusPresenter, statusRequest)

        verify(exactly = 1) { priorityService.changeStatus(statusPresenter, statusRequest.copy(reviewerId = null)) }
    }

    @Test
    fun `should not change the reading priority when the user is not allowed`() {
        deny()

        sut.changeReadingPriority(statusPresenter, statusRequest)

        verify(exactly = 0) { priorityService.changeStatus(any(), any()) }
    }

    @Test
    fun `should answer questions on the original study when the user is allowed`() {
        allow()
        every {
            batchAnswerQuestionService.batchAnswerQuestion(batchPresenter, batchRequest.copy(reviewerId = null))
        } just Runs

        sut.batchAnswerQuestion(batchPresenter, batchRequest)

        verify(exactly = 1) {
            batchAnswerQuestionService.batchAnswerQuestion(batchPresenter, batchRequest.copy(reviewerId = null))
        }
    }

    @Test
    fun `should not answer questions when the user is not allowed`() {
        deny()

        sut.batchAnswerQuestion(batchPresenter, batchRequest)

        verify(exactly = 0) { batchAnswerQuestionService.batchAnswerQuestion(any(), any()) }
    }

    @Test
    fun `should remove criteria of the original study when the user is allowed`() {
        allow()
        every { removeCriteriaService.removeCriteria(removePresenter, removeRequest.copy(reviewerId = null)) } just Runs

        sut.removeCriteria(removePresenter, removeRequest)

        verify(exactly = 1) { removeCriteriaService.removeCriteria(removePresenter, removeRequest.copy(reviewerId = null)) }
    }

    @Test
    fun `should not remove criteria when the user is not allowed`() {
        deny()

        sut.removeCriteria(removePresenter, removeRequest)

        verify(exactly = 0) { removeCriteriaService.removeCriteria(any(), any()) }
    }
}