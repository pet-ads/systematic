package br.all.application.protocol.repository

import br.all.application.protocol.create.ProtocolRequestModel
import br.all.domain.model.protocol.*
import br.all.domain.model.protocol.Criteria.CriteriaType
import br.all.domain.model.review.SystematicStudyId
import br.all.domain.shared.utils.Language
import java.util.*

fun Protocol.toDto() = ProtocolDto(
    id = protocolId.value,
    systematicStudy = systematicStudyId.value,

    goal = goal.toString(),
    justification = justification.toString(),

    researchQuestions = researchQuestions.map { it.description.toString() }
        .toSet(),
    keywords = keywords,
    searchString = searchString,
    informationSources = informationSources.map { it.searchSource }
        .toSet(),
    sourcesSelectionCriteria = sourcesSelectionCriteria.toString(),

    searchMethod = searchMethod.toString(),
    studiesLanguages = studiesLanguages.map { it.langType.name }
        .toSet(),
    studyTypeDefinition = studyTypeDefinition.toString(),

    selectionProcess = selectionProcess.toString(),
    selectionCriteria = selectionCriteria.map { it.description.toString() to it.type.name }
        .toSet(),

    dataCollectionProcess = dataCollectionProcess.toString(),
    analysisAndSynthesisProcess = analysisAndSynthesisProcess.toString(),

    extractionQuestions = extractionQuestions.map { it.value }.toSet(),
    robQuestions = robQuestions.map { it.value}.toSet(),

    picoc = picoc?.let { PicocDto(
        it.population.toString(),
        it.intervention.toString(),
        it.control.toString(),
        it.outcome.toString(),
        it.context.toString(),
    )},
)

fun Protocol.Companion.fromRequestModel(
    reviewId: UUID,
    requestModel: ProtocolRequestModel,
) = with(requestModel) {
    with(SystematicStudyId(reviewId), keywords)
        .researchesFor(goal)
        .because(justification)
        .toAnswer(researchQuestions.map { ResearchQuestion(it) }.toSet())
        .followingSearchProcess(searchMethod, searchString)
        .inSearchSources( informationSources.map { SearchSource(it) }.toSet()).selectedBecause(sourcesSelectionCriteria)
        .searchingStudiesIn( studiesLanguages.map { Language(Language.LangType.valueOf(it)) }.toSet(),studyTypeDefinition)
        .followingSelectionProcess(selectionProcess)
        .withElegibilityCriteria(
            selectionCriteria
            .map { (description, type) -> Criteria(description, CriteriaType.valueOf(type)) }
            .toSet())
        .followingDataCollectionProcess(dataCollectionProcess)
        .followingSynthesisProcess(analysisAndSynthesisProcess)
        .withPICOC(picoc?.let { Picoc(it.population, it.intervention, it.control, it.outcome, it.context) })
        .build()
}
