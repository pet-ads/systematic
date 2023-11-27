package br.all.application.protocol.repository

import br.all.application.protocol.create.ProtocolRequestModel
import br.all.domain.model.protocol.*
import br.all.domain.model.review.SystematicStudyId
import br.all.domain.shared.utils.Language
import br.all.domain.shared.utils.toPhrase
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
    id: UUID, 
    reviewId: UUID, 
    requestModel: ProtocolRequestModel,
) = with(requestModel) {
    write().identifiedBy(ProtocolId(id), SystematicStudyId(reviewId), keywords)
        .researchesFor(goal.toPhrase()).because(justification.toPhrase())
        .toAnswer(
            researchQuestions.map { ResearchQuestion(it.toPhrase()) }
                .toSet()
        ).searchProcessWillFollow(searchMethod.toPhrase(), searchString)
        .at(
            informationSources.map { SearchSource(it) }
                .toSet()
        ).selectedBecause(sourcesSelectionCriteria.toPhrase())
        .searchStudiesOf(
            studiesLanguages.map { Language(Language.LangType.valueOf(it)) }
                .toSet(),
            studyTypeDefinition.toPhrase(),
        ).selectionProcessWillFollowAs(selectionProcess.toPhrase())
        .selectStudiesBy(
            selectionCriteria
                .map { (description, type) -> Criteria(description.toPhrase(), Criteria.CriteriaType.valueOf(type)) }
                .toSet()
        ).collectDataBy(dataCollectionProcess.toPhrase())
        .analyseDataBy(analysisAndSynthesisProcess.toPhrase())
        .withPICOC(picoc?.let {
            Picoc(
                it.population.toPhrase(),
                it.intervention.toPhrase(),
                it.control.toPhrase(),
                it.outcome.toPhrase(),
                it.context?.toPhrase(),
            )
        }).finish()
}
