package br.all.application.protocol.update

import br.all.application.collaboration.repository.CollaborationRepository
import br.all.application.collaboration.repository.toDomain
import br.all.application.protocol.repository.ProtocolRepository
import br.all.application.protocol.repository.copyUpdates
import br.all.application.protocol.repository.fromDto
import br.all.application.protocol.repository.toDto
import br.all.application.protocol.update.UpdateProtocolService.RequestModel
import br.all.application.protocol.update.UpdateProtocolService.ResponseModel
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.review.repository.fromDto
import br.all.application.shared.presenter.prepareIfFailsPreconditions
import br.all.application.study.repository.StudyReviewRepository
import br.all.application.study.repository.fromDto
import br.all.application.study.repository.toDto
import br.all.application.user.CredentialsService
import br.all.domain.model.protocol.Protocol
import br.all.domain.model.review.SystematicStudy
import br.all.domain.model.review.toSystematicStudyId
import br.all.domain.model.study.StudyReview
import br.all.domain.services.ScoreCalculatorService

class UpdateProtocolServiceImpl(
    private val protocolRepository: ProtocolRepository,
    private val systematicStudyRepository: SystematicStudyRepository,
    private val credentialsService: CredentialsService,
    private val studyReviewRepository: StudyReviewRepository,
    private val scoreCalculatorService: ScoreCalculatorService,
    private val collaborationRepository: CollaborationRepository
) : UpdateProtocolService {
    override fun update(presenter: UpdateProtocolPresenter, request: RequestModel) {
        val userId = request.userId

        val systematicStudyId = request.systematicStudyId
        val user = credentialsService.loadCredentials(request.userId)?.toUser()

        val systematicStudyDto = systematicStudyRepository.findById(request.systematicStudyId)
        val systematicStudy = systematicStudyDto?.let { SystematicStudy.fromDto(it) }
        val collaborations = collaborationRepository
            .listAllCollaborationsBySystematicStudyId(request.systematicStudyId)
            .map { it.toDomain() }

        presenter.prepareIfFailsPreconditions(user, systematicStudy, collaborations = collaborations)
        if (presenter.isDone()) return

        val dto = protocolRepository.findById(systematicStudyId)
        val protocol = dto?.let { Protocol.fromDto(it) } ?: Protocol.write(
            systematicStudyId.toSystematicStudyId(),
            emptySet()
        ).build()

        val keywordsUpdated = request.keywords.isNotEmpty() && request.keywords != dto?.keywords

        protocol.copyUpdates(request)

        val updatedDto = protocol.toDto()
        if (updatedDto != dto) protocolRepository.saveOrUpdate(updatedDto)

        if (keywordsUpdated) {
            val studyReviews = studyReviewRepository.findAllFromReview(systematicStudyId)
                .map { StudyReview.fromDto(it) }

            val scoredStudyReviews = scoreCalculatorService.applyScoreToManyStudyReviews(studyReviews, protocol.keywords)
            studyReviewRepository.saveOrUpdateBatch(scoredStudyReviews.map { it.toDto() })
        }

        presenter.prepareSuccessView(ResponseModel(userId, systematicStudyId))
    }
}
