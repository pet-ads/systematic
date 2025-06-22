package br.all.application.report.find.service

import br.all.application.protocol.repository.ProtocolDto
import br.all.application.protocol.repository.ProtocolRepository
import br.all.application.report.find.presenter.ExportProtocolPresenter
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.review.repository.fromDto
import br.all.application.shared.exceptions.EntityNotFoundException
import br.all.application.shared.presenter.prepareIfFailsPreconditions
import br.all.application.user.CredentialsService
import br.all.domain.model.review.SystematicStudy
import br.all.domain.services.FormatterFactoryService
import br.all.domain.services.ProtocolFto

class ExportProtocolServiceImpl(
    private val credentialsService: CredentialsService,
    private val systematicStudyRepository: SystematicStudyRepository,
    private val formatterFactoryService: FormatterFactoryService,
    private val protocolRepository: ProtocolRepository,
): ExportProtocolService {
    override fun exportProtocol(presenter: ExportProtocolPresenter, request: ExportProtocolService.RequestModel) {
        val user = credentialsService.loadCredentials(request.userId)?.toUser()

        val systematicStudyDto = systematicStudyRepository.findById(request.systematicStudyId)
        val systematicStudy = systematicStudyDto?.let { SystematicStudy.fromDto(it) }

        presenter.prepareIfFailsPreconditions(user, systematicStudy)

        if(presenter.isDone()) return

        val protocolDto = protocolRepository.findById(request.systematicStudyId)!!

        val formattedProtocol = formatterFactoryService.format(
            request.format,
            protocolDto.toFto(),
        )

        if (formattedProtocol == null) {
            val message = "Invalid export format ${request.format}"
            presenter.prepareFailView(EntityNotFoundException(message))
            return
        }

        val response = ExportProtocolService.ResponseModel(
            userId = request.userId,
            systematicStudyId = request.systematicStudyId,
            format = request.format,
            formattedProtocol = formattedProtocol,
        )

        presenter.prepareSuccessView(response)
    }

    private fun ProtocolDto.toFto(): ProtocolFto {
        return ProtocolFto(
            id = this.id.toString(),
            systematicStudy = this.systematicStudy.toString(),
            goal = this.goal.orEmpty(),
            justification = this.justification.orEmpty(),
            researchQuestions = this.researchQuestions.toList(),
            keywords = this.keywords.toList(),
            searchString = this.searchString.orEmpty(),
            informationSources = this.informationSources.toList(),
            sourcesSelectionCriteria = this.sourcesSelectionCriteria.orEmpty(),
            searchMethod = this.searchMethod.orEmpty(),
            studiesLanguages = this.studiesLanguages.toList(),
            studyTypeDefinition = this.studyTypeDefinition.orEmpty(),
            selectionProcess = this.selectionProcess.orEmpty(),
            eligibilityCriteria = this.eligibilityCriteria.map { it.description },
            dataCollectionProcess = this.dataCollectionProcess.orEmpty(),
            analysisAndSynthesisProcess = this.analysisAndSynthesisProcess.orEmpty(),
            extractionQuestions = this.extractionQuestions.map { it.toString() },
            robQuestions = this.robQuestions.map { it.toString() },
            picoc = this.picoc?.toString() ?: ""
        )
    }
}