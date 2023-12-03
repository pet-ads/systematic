package br.all.application.protocol.repository

import br.all.application.protocol.create.ProtocolRequestModel
import br.all.domain.model.protocol.*
import br.all.domain.model.protocol.Criteria.CriteriaType
import br.all.domain.model.review.SystematicStudyId
import br.all.domain.shared.utils.Language
import java.util.*

fun Protocol.toDto() = ProtocolDto(
    id = id.value(),
    systematicStudy = systematicStudyId.value,

    goal = goal,
    justification = justification,

    researchQuestions = researchQuestions.map { it.description }.toSet(),
    keywords = keywords,
    searchString = searchString,
    informationSources = informationSources.map { it.searchSource }.toSet(),
    sourcesSelectionCriteria = sourcesSelectionCriteria,

    searchMethod = searchMethod,
    studiesLanguages = studiesLanguages.map { it.langType.name }.toSet(),
    studyTypeDefinition = studyTypeDefinition,

    selectionProcess = selectionProcess,
    selectionCriteria = selectionCriteria.map { it.description to it.type.name }
        .toSet(),

    dataCollectionProcess = dataCollectionProcess,
    analysisAndSynthesisProcess = analysisAndSynthesisProcess,

    extractionQuestions = extractionQuestions.map { it.value }.toSet(),
    robQuestions = robQuestions.map { it.value}.toSet(),

    picoc = picoc?.let { PicocDto(
        it.population,
        it.intervention,
        it.control,
        it.outcome,
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
