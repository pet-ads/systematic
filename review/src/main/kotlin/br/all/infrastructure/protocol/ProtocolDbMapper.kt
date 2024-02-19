package br.all.infrastructure.protocol

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
