package br.all.application.protocol.repository

import br.all.application.protocol.create.ProtocolRequestModel
import br.all.domain.model.protocol.*
import br.all.domain.model.review.ReviewId
import br.all.domain.shared.utils.Language
import br.all.domain.shared.utils.toPhrase
import java.util.*

fun Protocol.toDto() = ProtocolDto(
    id = protocolId.value,
    reviewId = reviewId.value,

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

    extractionQuestions = extractionQuestions.map { it.id }
        .toSet(),
    robQuestions = robQuestions.map { it.id }
        .toSet(),

    picoc = picoc?.let { PICOCDto(
        it.population.toString(),
        it.intervention.toString(),
        it.control.toString(),
        it.outcome.toString(),
        it.context.toString(),
    )},
)

fun Protocol.Companion.fromRequestModel(id: UUID, reviewId: UUID, requestModel: ProtocolRequestModel) = write()
    .identifiedBy(ProtocolId(id), ReviewId(reviewId), requestModel.keywords)
    .researchesFor(requestModel.goal.toPhrase()).because(requestModel.justification.toPhrase())
    .toAnswer(
        requestModel.researchQuestions
            .map { ResearchQuestion(it.toPhrase()) }
            .toSet()
    ).searchProcessWillFollow(requestModel.searchMethod.toPhrase(), requestModel.searchString)
    .at(
        requestModel.informationSources
            .map { SearchSource(it) }
            .toSet()
    ).selectedBecause(requestModel.sourcesSelectionCriteria.toPhrase())
    .searchStudiesOf(
        requestModel.studiesLanguages
            .map { Language(Language.LangType.valueOf(it)) }
            .toSet(),
        requestModel.studyTypeDefinition.toPhrase()
    ).selectionProcessWillFollowAs(requestModel.selectionProcess.toPhrase())
    .selectStudiesBy(
        requestModel.selectionCriteria
            .map { (description, type) -> Criteria(description.toPhrase(), Criteria.CriteriaType.valueOf(type)) }
            .toSet()
    ).collectDataBy(requestModel.dataCollectionProcess.toPhrase())
    .analyseDataBy(requestModel.analysisAndSynthesisProcess.toPhrase())
    .withPICOC(requestModel.picoc?.let {
        PICOC(
            it.population.toPhrase(),
            it.intervention.toPhrase(),
            it.control.toPhrase(),
            it.outcome.toPhrase(),
            it.context?.toPhrase(),
        )
    })
    .finish()
