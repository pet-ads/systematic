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
import br.all.domain.shared.presenter.GenericPresenter
import br.all.domain.shared.user.Role
import java.util.UUID

class AgreementStudyReviewService(
    private val authorizationService: AuthorizationService,
    private val selectionService: UpdateStudyReviewSelectionService,
    private val extractionService: UpdateStudyReviewExtractionService,
    private val priorityService: UpdateStudyReviewPriorityService,
    private val batchAnswerQuestionService: BatchAnswerQuestionService,
    private val removeCriteriaService: RemoveCriteriaService,
) {
    fun changeSelectionStatus(
        presenter: UpdateStudyReviewStatusPresenter,
        request: UpdateStudyReviewStatusService.RequestModel
    ) {
        if (!canResolve(presenter, request.userId, request.systematicStudyId)) return
        selectionService.changeStatus(presenter, request.copy(reviewerId = null))
    }

    fun changeExtractionStatus(
        presenter: UpdateStudyReviewStatusPresenter,
        request: UpdateStudyReviewStatusService.RequestModel
    ) {
        if (!canResolve(presenter, request.userId, request.systematicStudyId)) return
        extractionService.changeStatus(presenter, request.copy(reviewerId = null))
    }

    fun changeReadingPriority(
        presenter: UpdateStudyReviewStatusPresenter,
        request: UpdateStudyReviewStatusService.RequestModel
    ) {
        if (!canResolve(presenter, request.userId, request.systematicStudyId)) return
        priorityService.changeStatus(presenter, request.copy(reviewerId = null))
    }

    fun batchAnswerQuestion(
        presenter: BatchAnswerQuestionPresenter,
        request: BatchAnswerQuestionService.RequestModel
    ) {
        if (!canResolve(presenter, request.userId, request.systematicStudyId)) return
        batchAnswerQuestionService.batchAnswerQuestion(presenter, request.copy(reviewerId = null))
    }

    fun removeCriteria(
        presenter: RemoveCriteriaPresenter,
        request: RemoveCriteriaService.RequestModel
    ) {
        if (!canResolve(presenter, request.userId, request.systematicStudyId)) return
        removeCriteriaService.removeCriteria(presenter, request.copy(reviewerId = null))
    }

    private fun canResolve(presenter: GenericPresenter<*>, userId: UUID, systematicStudyId: UUID): Boolean =
        authorizationService.authorize(presenter, userId, systematicStudyId, RESOLVER_ROLES) != null

    companion object {
        val RESOLVER_ROLES = setOf(Role.OWNER)
    }
}