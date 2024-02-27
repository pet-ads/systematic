package br.all.application.protocol.repository

import br.all.application.protocol.create.CreateProtocolService.RequestModel
import br.all.domain.model.protocol.*
import br.all.domain.model.review.SystematicStudyId
import br.all.domain.shared.valueobject.Language
import br.all.domain.shared.valueobject.Language.LangType

fun Protocol.toDto() = ProtocolDto(
    id = id.value(),
    systematicStudy = systematicStudyId.value(),

    goal = goal,
    justification = justification,

    researchQuestions = researchQuestions.map { it.toString() }.toSet(),
    keywords = keywords,
    searchString = searchString,
    informationSources = informationSources.map { it.toString() }.toSet(),
    sourcesSelectionCriteria = sourcesSelectionCriteria,

    searchMethod = searchMethod,
    studiesLanguages = studiesLanguages.map { it.langType.name }.toSet(),
    studyTypeDefinition = studyTypeDefinition,

    selectionProcess = selectionProcess,
    selectionCriteria = eligibilityCriteria.map { it.description to it.type.name }
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
        it.context,
    )},
)

fun Protocol.Companion.fromRequestModel(request: RequestModel) = with(request) {
    write(SystematicStudyId(systematicStudyId), keywords)
        .researchesFor(goal)
        .because(justification)
        .followingSearchProcess(searchMethod, searchString)
        .inSearchSources( informationSources.map { it.toSearchSource() }.toSet())
        .selectedBecause(sourcesSelectionCriteria)
        .searchingStudiesIn(studiesLanguages.map { Language(LangType.valueOf(it)) }.toSet(), studyTypeDefinition)
        .followingSelectionProcess(selectionProcess)
        .followingDataCollectionProcess(dataCollectionProcess)
        .followingSynthesisProcess(analysisAndSynthesisProcess)
        .build()
}

fun Protocol.Companion.fromDto(dto: ProtocolDto) = with(dto) {
    Protocol.with(SystematicStudyId(systematicStudy), keywords)
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
