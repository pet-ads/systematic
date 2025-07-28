package br.all.application.report.find.service

import br.all.application.collaboration.repository.CollaborationRepository
import br.all.application.collaboration.repository.toDomain
import br.all.application.protocol.repository.ProtocolRepository
import br.all.application.report.find.presenter.FindSourcePresenter
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.review.repository.fromDto
import br.all.application.shared.exceptions.EntityNotFoundException
import br.all.application.shared.presenter.prepareIfFailsPreconditions
import br.all.application.study.repository.StudyReviewDto
import br.all.application.study.repository.StudyReviewRepository
import br.all.application.user.CredentialsService
import br.all.domain.model.review.SystematicStudy
import br.all.domain.model.study.SelectionStatus

class FindSourceServiceImpl(
    private val protocolRepository: ProtocolRepository,
    private val systematicStudyRepository: SystematicStudyRepository,
    private val studyReviewRepository: StudyReviewRepository,
    private val credentialsService: CredentialsService,
    private val collaborationRepository: CollaborationRepository,
): FindSourceService {
    override fun findSource(presenter: FindSourcePresenter, request: FindSourceService.RequestModel) {
        val user = credentialsService.loadCredentials(request.userId)?.toUser()

        val systematicStudyDto = systematicStudyRepository.findById(request.systematicStudyId)
        val systematicStudy = systematicStudyDto?.let { SystematicStudy.fromDto(it) }
        val collaborations = collaborationRepository
            .listAllCollaborationsBySystematicStudyId(request.systematicStudyId)
            .map { it.toDomain() }

        presenter.prepareIfFailsPreconditions(user, systematicStudy, collaborations = collaborations)

        if(presenter.isDone()) return

        val protocolDto = protocolRepository.findById(request.systematicStudyId)

        if (protocolDto == null) {
            val message = "Unable to find protocol at '${request.systematicStudyId}'."
            presenter.prepareFailView(EntityNotFoundException(message))
            return
        }

        if (request.source !in protocolDto.informationSources) {
            val message = "Search source does not exist in protocol '${request.systematicStudyId}'."
            presenter.prepareFailView(EntityNotFoundException(message))
            return
        }

        val studyReviewBySource = studyReviewRepository.findAllBySource(request.systematicStudyId, request.source)
        val grouped = studyReviewBySource.groupBy { it.selectionStatus }

        val response = FindSourceService.ResponseModel(
            userId = request.userId,
            systematicStudyId = request.systematicStudyId,
            source = request.source,
            included = grouped.idsFor(SelectionStatus.INCLUDED),
            excluded = grouped.idsFor(SelectionStatus.EXCLUDED),
            duplicated = grouped.idsFor(SelectionStatus.DUPLICATED),
            totalOfStudies = studyReviewBySource.size,
        )

        presenter.prepareSuccessView(response)
    }

    private fun Map<String, List<StudyReviewDto>>.idsFor(status: SelectionStatus) =
        this[status.toString()]?.map { it.studyReviewId } ?: emptyList()
}