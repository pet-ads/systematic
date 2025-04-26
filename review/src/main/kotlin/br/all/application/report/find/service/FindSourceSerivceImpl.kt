package br.all.application.report.find.service

import br.all.application.protocol.repository.ProtocolRepository
import br.all.application.report.find.presenter.FindSourcePresenter
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.review.repository.fromDto
import br.all.application.shared.presenter.prepareIfFailsPreconditions
import br.all.application.study.repository.StudyReviewDto
import br.all.application.study.repository.StudyReviewRepository
import br.all.application.user.CredentialsService
import br.all.domain.model.review.SystematicStudy
import br.all.domain.model.study.SelectionStatus
import br.all.infrastructure.study.StudyReviewDocument
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException
import java.util.*
import kotlin.NoSuchElementException

class FindSourceSerivceImpl(
    private val protocolRepository: ProtocolRepository,
    private val systematicStudyRepository: SystematicStudyRepository,
    private val studyReviewRepository: StudyReviewRepository,
    private val credentialsService: CredentialsService,
): FindSourceService {
    override fun findSource(presenter: FindSourcePresenter, request: FindSourceService.RequestModel) {
        val user = credentialsService.loadCredentials(request.userId)?.toUser()

        val systematicStudyDto = systematicStudyRepository.findById(request.systematicStudyId)
        val systematicStudy = systematicStudyDto?.let { SystematicStudy.fromDto(it) }

        presenter.prepareIfFailsPreconditions(user, systematicStudy)

        if(presenter.isDone()) return

        val protocolDto = protocolRepository.findById(request.systematicStudyId)

        if (protocolDto == null) {
            val message = "Unable to find protocol at '${request.systematicStudyId}'."
            presenter.prepareFailView(NoSuchElementException(message))
            return
        }

        if (request.source !in protocolDto.informationSources) {
            val message = "Search source does not exist in protocol '${request.systematicStudyId}'."
            presenter.prepareFailView(NoSuchElementException(message))
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