package br.all.application.study.update.implementation

import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.review.repository.fromDto
import br.all.application.reviewmode.repository.ReviewerStudyRepository
import br.all.domain.shared.exception.EntityNotFoundException
import br.all.application.shared.presenter.prepareIfFailsPreconditions
import br.all.application.study.access.StudyReviewAccess
import br.all.application.study.repository.StudyReviewRepository
import br.all.application.study.repository.fromDto
import br.all.application.study.repository.toDto
import br.all.application.study.update.interfaces.RemoveCriteriaPresenter
import br.all.application.study.update.interfaces.RemoveCriteriaService
import br.all.application.user.CredentialsService
import br.all.domain.model.protocol.Criterion
import br.all.domain.model.review.SystematicStudy
import br.all.domain.model.study.StudyReview
import br.all.domain.model.study.StudyReviewStage

class RemoveCriteriaServiceImpl(
    private val studyReviewRepository: StudyReviewRepository,
    private val systematicStudyRepository: SystematicStudyRepository,
    private val credentialsService: CredentialsService,
    reviewerStudyRepository: ReviewerStudyRepository? = null,
): RemoveCriteriaService {

    private val studyReviewAccess = StudyReviewAccess(studyReviewRepository, reviewerStudyRepository)

    override fun removeCriteria(presenter: RemoveCriteriaPresenter, request: RemoveCriteriaService.RequestModel) {
        val user = credentialsService.loadCredentials(request.userId)?.toUser()

        val systematicStudyDto = systematicStudyRepository.findById(request.systematicStudyId)
        val systematicStudy = systematicStudyDto?.let { SystematicStudy.fromDto(it) }

        presenter.prepareIfFailsPreconditions(user, systematicStudy)
        if (presenter.isDone()) return

        studyReviewAccess.validate(request.userId, request.reviewerId, request.stage)?.let {
            presenter.prepareFailView(it)
            return
        }

        val target = studyReviewAccess.open(request.systematicStudyId, request.studyId, request.reviewerId, request.stage)
        if (target == null) {
            presenter.prepareFailView(
                EntityNotFoundException("Study review of id ${request.systematicStudyId} not found.")
            )
            return
        }

        val studyReview = StudyReview.fromDto(target.dto)

        val criteria = when (request.stage) {
            StudyReviewStage.SELECTION -> studyReview.selectionCriteria
            StudyReviewStage.EXTRACTION -> studyReview.extractionCriteria
        }

        val inclusionCriteria = criteria
            .filter { it.type == Criterion.CriterionType.INCLUSION }
            .map { it.description }

        val exclusionCriteria = criteria
            .filter { it.type == Criterion.CriterionType.EXCLUSION }
            .map { it.description }

        request.criteria
            .filter { it.isNotBlank() }
            .forEach { criterionString ->

                when (request.stage) {

                    StudyReviewStage.SELECTION -> {
                        studyReview.selectionCriteria
                            .filter { it.description == criterionString }
                            .forEach(studyReview::removeSelectionCriterion)
                    }

                    StudyReviewStage.EXTRACTION -> {
                        studyReview.extractionCriteria
                            .filter { it.description == criterionString }
                            .forEach(studyReview::removeExtractionCriterion)
                    }
                }
            }

        target.save(studyReview.toDto())

        presenter.prepareSuccessView(
            RemoveCriteriaService.ResponseModel(
                request.systematicStudyId,
                request.studyId,
                request.stage,
                inclusionCriteria,
                exclusionCriteria,
            )
        )
    }
}