package br.all.application.protocol.util

import br.all.application.protocol.repository.*
import br.all.domain.model.protocol.Protocol
import br.all.domain.model.review.toSystematicStudyId
import br.all.domain.shared.utils.wordsList
import io.github.serpro69.kfaker.Faker
import java.util.*
import br.all.application.protocol.find.FindProtocolService.RequestModel as FindRequestModel
import br.all.application.protocol.find.FindProtocolService.ResponseModel as FindOneResponseModel
import br.all.application.protocol.update.UpdateProtocolService.RequestModel as UpdateRequestModel
import br.all.application.protocol.update.UpdateProtocolService.ResponseModel as UpdateResponseModel

class TestDataFactory {
    val researcher: UUID = UUID.randomUUID()
    val systematicStudy: UUID = UUID.randomUUID()
    val collaboration: UUID = UUID.randomUUID()

    private val faker = Faker()

    fun protocolDto(
        systematicStudy: UUID = this.systematicStudy,
        goal: String? = text(),
        justification: String? = text(),
        researchQuestions: Set<String> = emptySet(),
        keywords: Set<String> = emptySet(),

        searchString: String? = null,
        informationSources: Set<String> = emptySet(),
        sourcesSelectionCriteria: String? = null,
        searchMethod: String? = null,

        studiesLanguages: Set<String> = emptySet(),
        studyTypeDefinition: String? = null,

        selectionProcess: String? = null,
        eligibilityCriteria: Set<CriterionDto> = emptySet(),

        dataCollectionProcess: String? = null,
        extractionQuestions: Set<UUID> = emptySet(),

        analysisAndSynthesisProcess: String? = null,
        robQuestions: Set<UUID> = emptySet(),

        picoc: PicocDto? = null,
    ) = ProtocolDto(
        systematicStudy,
        systematicStudy,

        goal,
        justification,
        researchQuestions,
        keywords,

        searchString,
        informationSources,
        sourcesSelectionCriteria,
        searchMethod,

        studiesLanguages,
        studyTypeDefinition,

        selectionProcess,
        eligibilityCriteria,

        dataCollectionProcess,
        analysisAndSynthesisProcess,

        extractionQuestions,
        robQuestions,

        picoc,
    )

    fun text() = faker.wordsList(minSize = 1, maxSize = 5).joinToString(" ")

    fun findRequestModel(
        researcher: UUID = this.researcher,
        systematicStudy: UUID = this.systematicStudy
    ) = FindRequestModel(researcher, systematicStudy)

    fun findResponseModel(
        researcher: UUID = this.researcher,
        systematicStudy: UUID = this.systematicStudy,
        dto: ProtocolDto = protocolDto(),
    ) = FindOneResponseModel(researcher, systematicStudy, dto)

    fun updateRequestModel(
        researcher: UUID = this.researcher,
        systematicStudy: UUID = this.systematicStudy,
        goal: String? = text(),
        justification: String? = text(),
        researchQuestions: Set<String> = emptySet(),
        keywords: Set<String> = emptySet(),

        searchString: String? = text(),
        informationSources: Set<String> = emptySet(),
        sourcesSelectionCriteria: String? = text(),
        searchMethod: String? = text(),

        studiesLanguages: Set<String> = emptySet(),
        studyTypeDefinition: String? = text(),

        selectionProcess: String? = text(),
        eligibilityCriteria: Set<CriterionDto> = emptySet(),

        dataCollectionProcess: String? = text(),
        analysisAndSynthesisProcess: String? = text(),
    ) = UpdateRequestModel(
        researcher,
        systematicStudy,

        goal,
        justification,
        researchQuestions,
        keywords,

        searchString,
        informationSources,
        sourcesSelectionCriteria,
        searchMethod,

        studiesLanguages,
        studyTypeDefinition,

        selectionProcess,
        eligibilityCriteria,

        dataCollectionProcess,
        analysisAndSynthesisProcess,
    )

    fun updateResponseModel(
        researcher: UUID = this.researcher,
        systematicStudy: UUID = this.systematicStudy,
    ) = UpdateResponseModel(researcher, systematicStudy)

    fun updatedDto(dto: ProtocolDto, request: UpdateRequestModel) = Protocol.fromDto(dto)
        .copyUpdates(request)
        .toDto()

    fun emptyUpdateRequest(
        dto: ProtocolDto,
        researcher: UUID = this.researcher,
        systematicStudy: UUID = this.systematicStudy,
    ) = UpdateRequestModel(
        researcher,
        systematicStudy,
        researchQuestions = dto.researchQuestions,
        keywords = dto.keywords,
        informationSources = dto.informationSources,
        studiesLanguages = dto.studiesLanguages,
        eligibilityCriteria = dto.eligibilityCriteria,
    )

    fun protocolCreatedWithUpdates(
        request: UpdateRequestModel,
        systematicStudy: UUID = this.systematicStudy,
    ) = Protocol.write(systematicStudy.toSystematicStudyId(), emptySet())
        .build()
        .copyUpdates(request)
        .toDto()

    operator fun component1() = researcher

    operator fun component2() = systematicStudy

    operator fun component3() = collaboration
}
