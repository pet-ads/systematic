package br.all.application.protocol.update

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
) : UpdateProtocolService {
    override fun update(presenter: UpdateProtocolPresenter, request: RequestModel) {
        val userId = request.userId
        val user = credentialsService.loadCredentials(userId)?.toUser()

        val systematicStudyId = request.systematicStudyId
        val systematicStudyDto = systematicStudyRepository.findById(systematicStudyId)
        val systematicStudy = systematicStudyDto?.let { SystematicStudy.fromDto(it) }

        presenter.prepareIfFailsPreconditions(user, systematicStudy)
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
            val scoreCalculatorService = ScoreCalculatorService(updatedDto.keywords)

            val studyReviewDtos = studyReviewRepository.findAllFromReview(systematicStudyId)

            val studyReviews = studyReviewDtos.map { StudyReview.fromDto(it) }

            val updatedStudyReviews = scoreCalculatorService.applyScoreToManyStudyReviews(studyReviews)

            val updatedStudyReviewDtos = updatedStudyReviews.map { it.toDto() }
            studyReviewRepository.saveOrUpdateBatch(updatedStudyReviewDtos)
        }

        presenter.prepareSuccessView(ResponseModel(userId, systematicStudyId))
    }
}
