package br.all.application.study.update.implementation

import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.review.repository.fromDto
import br.all.application.reviewmode.repository.ReviewerStudyRepository
import br.all.domain.shared.exception.EntityNotFoundException
import br.all.domain.shared.exception.UnauthorizedUserException
import br.all.application.shared.presenter.prepareIfFailsPreconditions
import br.all.application.study.majority.MajorityDecisionApplier
import br.all.application.study.repository.StudyReviewRepository
import br.all.application.study.repository.fromDto
import br.all.application.study.repository.toDto
import br.all.application.study.update.interfaces.UpdateStudyReviewStatusService.RequestModel
import br.all.application.study.update.interfaces.UpdateStudyReviewStatusPresenter
import br.all.application.study.update.interfaces.UpdateStudyReviewStatusService
import br.all.application.study.update.interfaces.UpdateStudyReviewStatusService.ResponseModel
import br.all.application.user.CredentialsService
import br.all.domain.model.protocol.Criterion
import br.all.domain.model.review.SystematicStudy
import br.all.domain.model.study.StudyReview
import br.all.domain.model.study.StudyReviewStage

class UpdateStudyReviewExtractionService(
    private val systematicStudyRepository: SystematicStudyRepository,
    private val studyReviewRepository: StudyReviewRepository,
    private val credentialsService: CredentialsService,
    private val reviewerStudyRepository: ReviewerStudyRepository? = null,
    private val majorityApplier: MajorityDecisionApplier? = null,
) : UpdateStudyReviewStatusService {

    override fun changeStatus(presenter: UpdateStudyReviewStatusPresenter, request: RequestModel){
        val user = credentialsService.loadCredentials(request.userId)?.toUser()

        val systematicStudyDto = systematicStudyRepository.findById(request.systematicStudyId)
        val systematicStudy = systematicStudyDto?.let { SystematicStudy.fromDto(it) }

        presenter.prepareIfFailsPreconditions(user, systematicStudy)

        if(presenter.isDone()) return

        val reviewerId = request.reviewerId
        if (reviewerId != null && reviewerId != request.userId) {
            presenter.prepareFailView(
                UnauthorizedUserException("A reviewer can only classify the studies assigned to himself.")
            )
            return
        }

        for (studyId in request.studyReviewId) {
            val reviewerStudyDto = reviewerId?.let {
                requireNotNull(reviewerStudyRepository) { "ReviewerStudyRepository is required to classify as a reviewer." }
                    .findById(request.systematicStudyId, it, studyId, StudyReviewStage.EXTRACTION)
            }
            val studyReviewDto =
                if (reviewerId == null) studyReviewRepository.findById(request.systematicStudyId, studyId)
                else reviewerStudyDto?.studyReview
            if(studyReviewDto == null) {
                presenter.prepareFailView(EntityNotFoundException("Study review of id ${request.systematicStudyId} not found."))
                return
            }

            val newStatus = request.status.uppercase()
            if(newStatus == "DUPLICATED" ) {
                val message = "Duplication request must indicate the duplicate study. Please use the proper feature."
                presenter.prepareFailView(IllegalArgumentException(message))
                return
            }

            val studyReview = StudyReview.fromDto(studyReviewDto)
            when(newStatus){
                "UNCLASSIFIED" -> studyReview.declassifyInExtraction()
                "INCLUDED" -> studyReview.includeInExtraction()
                "EXCLUDED" -> studyReview.excludeInExtraction()
                else -> throw IllegalArgumentException("Unknown study review status: ${request.status}.")
            }

            request.criteria.forEach { criterionString ->
                val trimmed = criterionString.trim()
                if (trimmed.isBlank()) {
                    throw IllegalArgumentException("Criterion string cannot be blank")
                }
                val criterion = if (newStatus == "EXCLUDED") {
                    Criterion.toExclude(trimmed)
                } else {
                    Criterion.toInclude(trimmed)
                }
                studyReview.addExtractionCriterion(criterion)
            }

            if (reviewerStudyDto != null) {
                requireNotNull(reviewerStudyRepository)
                    .saveOrUpdate(reviewerStudyDto.copy(studyReview = studyReview.toDto()))
                majorityApplier?.apply(request.systematicStudyId, studyId, StudyReviewStage.EXTRACTION)
            } else {
                studyReviewRepository.saveOrUpdate(studyReview.toDto())
            }
        }

        presenter.prepareSuccessView(ResponseModel(request.userId, request.systematicStudyId, request.studyReviewId))
    }
}