package br.all.application.study.update.implementation

import br.all.application.collaboration.repository.CollaborationRepository
import br.all.application.collaboration.repository.toDomain
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.review.repository.fromDto
import br.all.application.shared.exceptions.EntityNotFoundException
import br.all.application.shared.presenter.prepareIfFailsPreconditions
import br.all.application.study.repository.StudyReviewRepository
import br.all.application.study.repository.fromDto
import br.all.application.study.repository.toDto
import br.all.application.study.update.interfaces.RemoveCriteriaPresenter
import br.all.application.study.update.interfaces.RemoveCriteriaService
import br.all.application.user.CredentialsService
import br.all.domain.model.protocol.Criterion
import br.all.domain.model.review.SystematicStudy
import br.all.domain.model.study.StudyReview

class RemoveCriteriaServiceImpl(
    private val studyReviewRepository: StudyReviewRepository,
    private val systematicStudyRepository: SystematicStudyRepository,
    private val credentialsService: CredentialsService,
    private val collaborationRepository: CollaborationRepository
): RemoveCriteriaService {
    override fun removeCriteria(presenter: RemoveCriteriaPresenter, request: RemoveCriteriaService.RequestModel) {
        val user = credentialsService.loadCredentials(request.userId)?.toUser()

        val systematicStudyDto = systematicStudyRepository.findById(request.systematicStudyId)
        val systematicStudy = systematicStudyDto?.let { SystematicStudy.fromDto(it) }
        val collaborations = collaborationRepository
            .listAllCollaborationsBySystematicStudyId(request.systematicStudyId)
            .map { it.toDomain() }

        presenter.prepareIfFailsPreconditions(user, systematicStudy, collaborations = collaborations)
        if (presenter.isDone()) return

        val studyReviewDto = studyReviewRepository.findById(request.systematicStudyId, request.studyId)
        if (studyReviewDto == null) {
            presenter.prepareFailView(
                EntityNotFoundException("Study review of id ${request.systematicStudyId} not found.")
            )
            return
        }

        val studyReview = StudyReview.fromDto(studyReviewDto)

        val inclusionCriteria: List<String> = studyReview.criteria
            .filter  { it.type == Criterion.CriterionType.INCLUSION }
            .map     { it.description }

        val exclusionCriteria: List<String> = studyReview.criteria
            .filter  { it.type == Criterion.CriterionType.EXCLUSION }
            .map     { it.description }

        request.criteria
            .filter { it.isNotBlank() }
            .forEach { criterionString ->
                studyReview.criteria
                    .filter { it.description == criterionString }
                    .forEach { matching ->
                        studyReview.removeCriterion(matching)
                    }
            }

        studyReviewRepository.saveOrUpdate(studyReview.toDto())

        presenter.prepareSuccessView(
            RemoveCriteriaService.ResponseModel(
                request.systematicStudyId,
                request.studyId,
                inclusionCriteria,
                exclusionCriteria,
            )
        )
    }
}