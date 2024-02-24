package br.all.infrastructure.protocol

import br.all.application.protocol.repository.PicocDto
import br.all.application.protocol.repository.ProtocolDto

fun ProtocolDto.toDocument() = ProtocolDocument(
    id,

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
    selectionCriteria,

    dataCollectionProcess,
    analysisAndSynthesisProcess,

    extractionQuestions,
    robQuestions,

    picoc?.population,
    picoc?.intervention,
    picoc?.control,
    picoc?.outcome,
    picoc?.context,
)

fun ProtocolDocument.toDto() = ProtocolDto(
    id = id,
    systematicStudy = id,

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
    selectionCriteria,

    dataCollectionProcess,
    analysisAndSynthesisProcess,

    extractionQuestions,
    robQuestions,

    getPicoc(),
)

private fun ProtocolDocument.getPicoc(): PicocDto? {
    if (population == null || intervention == null || control == null || outcome == null)
        return null
    return PicocDto(population, intervention, control, outcome, context)
}
