package br.all.application.protocol.update

import br.all.application.protocol.repository.ProtocolRepository
import br.all.application.protocol.repository.fromDto
import br.all.application.protocol.repository.toDto
import br.all.application.protocol.update.UpdateProtocolService.RequestModel
import br.all.application.protocol.update.UpdateProtocolService.ResponseModel
import br.all.application.researcher.credentials.ResearcherCredentialsService
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.shared.presenter.PreconditionChecker
import br.all.domain.model.protocol.*
import br.all.domain.model.protocol.Criterion.CriterionType
import br.all.domain.model.researcher.toResearcherId
import br.all.domain.model.review.toSystematicStudyId
import br.all.domain.shared.valueobject.Language
import br.all.domain.shared.valueobject.Language.LangType

class UpdateProtocolServiceImpl(
    private val protocolRepository: ProtocolRepository,
    private val systematicStudyRepository: SystematicStudyRepository,
    private val credentialsService: ResearcherCredentialsService,
): UpdateProtocolService {
    override fun update(presenter: UpdateProtocolPresenter, request: RequestModel) {
        val (researcher, systematicStudy) = request

        PreconditionChecker(systematicStudyRepository, credentialsService).also {
            it.prepareIfViolatesPreconditions(presenter, researcher.toResearcherId(), systematicStudy.toSystematicStudyId())
        }
        if (presenter.isDone()) return

        val dto = protocolRepository.findById(systematicStudy)
        val protocol = dto?.let { Protocol.fromDto(it) } ?: Protocol.write(
            systematicStudy.toSystematicStudyId(),
            emptySet()
        ).build()
        protocol.copyUpdates(request)

        val updatedDto = protocol.toDto()
        if (updatedDto == dto) return

        protocolRepository.saveOrUpdate(updatedDto)
        presenter.prepareSuccessView(ResponseModel(researcher, systematicStudy))
    }

    private fun Protocol.copyUpdates(request: RequestModel) = apply {
        goal = request.goal ?: goal
        justification = request.justification ?: justification
        request.researchQuestions
            .map { it.toResearchQuestion() }
            .toSet()
            .let { replaceResearchQuestions(it) }
        replaceKeywords(request.keywords)

        searchString = request.searchString ?: searchString
        request.informationSources
            .map { it.toSearchSource() }
            .toSet()
            .let { replaceInformationSources(it) }
        sourcesSelectionCriteria = request.sourcesSelectionCriteria ?: sourcesSelectionCriteria
        searchMethod = request.searchMethod ?: searchMethod

        request.studiesLanguages
            .map { Language(LangType.valueOf(it)) }
            .toSet()
            .let { replaceLanguages(it) }
        studyTypeDefinition = request.studyTypeDefinition ?: studyTypeDefinition

        selectionProcess = request.selectionProcess ?: selectionProcess
        request.eligibilityCriteria
            .map { (description, type) -> Criterion(description, CriterionType.valueOf(type)) }
            .toSet()
            .let { replaceEligibilityCriteria(it) }

        dataCollectionProcess = request.dataCollectionProcess ?: dataCollectionProcess
        analysisAndSynthesisProcess = request.analysisAndSynthesisProcess ?: analysisAndSynthesisProcess

        picoc = request.picoc?.let { Picoc.fromDto(it) }
    }
}
