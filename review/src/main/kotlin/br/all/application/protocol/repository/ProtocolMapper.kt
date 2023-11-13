package br.all.application.protocol.repository

import br.all.application.protocol.create.ProtocolRequestModel
import br.all.domain.model.protocol.*
import br.all.domain.model.review.ReviewId
import br.all.domain.shared.utils.Language
import br.all.domain.shared.utils.Phrase
import java.util.*

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

fun Protocol.Companion.fromRequestModel(id: UUID, reviewId: UUID, requestModel: ProtocolRequestModel) = write()
    .identifiedBy(ProtocolId(id), ReviewId(reviewId), requestModel.keywords)
    .researchesFor(Phrase(requestModel.goal)).because(Phrase(requestModel.justification))
    .toAnswer(
        requestModel.researchQuestions
            .map { ResearchQuestion(Phrase(it)) }
            .toSet()
    ).searchProcessWillFollow(Phrase(requestModel.searchMethod), requestModel.searchString)
    .at(
        requestModel.informationSources
            .map { SearchSource(it) }
            .toSet()
    ).selectedBecause(Phrase(requestModel.sourcesSelectionCriteria))
    .searchStudiesOf(
        requestModel.studiesLanguages
            .map { Language(Language.LangType.valueOf(it)) }
            .toSet(),
        Phrase(requestModel.studyTypeDefinition)
    ).selectionProcessWillFollowAs(Phrase(requestModel.selectionProcess))
    .selectStudiesBy(
        requestModel.selectionCriteria
            .map { (description, type) -> Criteria(Phrase(description), Criteria.CriteriaType.valueOf(type)) }
            .toSet()
    ).collectDataBy(Phrase(requestModel.dataCollectionProcess))
    .analyseDataBy(Phrase(requestModel.analysisAndSynthesisProcess))
    .finish()
