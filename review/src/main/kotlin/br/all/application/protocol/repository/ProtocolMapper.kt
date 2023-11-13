package br.all.application.protocol.repository

import br.all.domain.model.protocol.Protocol

fun Protocol.toDto() = ProtocolDto(
    id = protocolId.value,
    reviewId = reviewId.value,

    goal = goal.text,
    justification = justification.text,

    researchQuestions = researchQuestions.map { it.description.text }
        .toSet(),
    keywords = keywords,
    searchString = searchString,
    informationSources = informationSources.map { it.searchSource }
        .toSet(),
    sourcesSelectionCriteria = sourcesSelectionCriteria.text,

    searchMethod = searchMethod.text,
    studiesLanguages = studiesLanguages.map { it.langType.name }
        .toSet(),
    studyTypeDefinition = studyTypeDefinition.text,

    selectionProcess = selectionProcess.text,
    selectionCriteria = selectionCriteria.map { it.description.text to it.type.name }
        .toSet(),

    dataCollectionProcess = dataCollectionProcess.text,
    analysisAndSynthesisProcess = analysisAndSynthesisProcess.text,

    extractionQuestions = extractionQuestions.map { it.id }
        .toSet(),
    robQuestions = robQuestions.map { it.id }
        .toSet(),

    picoc = picoc?.let { PICOCDto(
        it.population.text,
        it.intervention.text,
        it.control.text,
        it.outcome.text,
        it.context?.text,
    )},
)
