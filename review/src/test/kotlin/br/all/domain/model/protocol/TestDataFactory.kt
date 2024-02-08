package br.all.domain.model.protocol

import br.all.domain.model.question.QuestionId
import br.all.domain.model.review.SystematicStudyId
import br.all.domain.shared.utils.wordsList
import br.all.domain.shared.valueobject.Language
import br.all.domain.shared.valueobject.Language.LangType
import io.github.serpro69.kfaker.Faker
import java.util.*

class TestDataFactory {
    private val faker = Faker()
    
    fun createProtocol(
        systematicStudy: SystematicStudyId = systematicStudy(),
        keywords: Set<String> = keywords(),
        goal: String? = text(),
        justification: String? = text(),
        researchQuestions: Set<ResearchQuestion> = researchQuestions(),
        searchMethod: String = text(),
        searchString: String? = text(),
        informationSources: Set<SearchSource> = informationSources(),
        sourcesSelectionCriteria: String? = text(),
        languages: Set<Language> = languages(),
        studyTypeDefinition: String = text(),
        selectionProcess: String = text(),
        eligibilityCriteria: Set<Criterion> = eligibilityCriteria(),
        dataCollectionProcess: String = text(),
        analysisAndSynthesis: String = text(),
        extractionQuestions: Set<QuestionId> = questionsSet(),
        robQuestions: Set<QuestionId> = questionsSet(),
    ) = Protocol.write(systematicStudy, keywords)
        .researchesFor(goal)
        .because(justification)
        .toAnswer(researchQuestions)
        .followingSearchProcess(searchMethod, searchString)
        .inSearchSources(informationSources)
        .selectedBecause(sourcesSelectionCriteria)
        .searchingStudiesIn(languages, studyTypeDefinition)
        .followingSelectionProcess(selectionProcess)
        .withEligibilityCriteria(eligibilityCriteria)
        .followingDataCollectionProcess(dataCollectionProcess)
        .followingSynthesisProcess(analysisAndSynthesis)
        .extractDataByAnswering(extractionQuestions)
        .qualityFormConsiders(robQuestions)
        .build()

    fun systematicStudy() = SystematicStudyId(UUID.randomUUID())

    fun keywords(length: Int = 5) = faker.wordsList(length).toSet()

    fun text() = faker.wordsList(minSize = 1, maxSize = 5)
        .joinToString(" ")
        .replaceFirstChar { it - 32 }

    fun researchQuestions(length: Int = 5) = List(length) {
        text().toResearchQuestion()
    }.toSet()

    fun informationSources(length: Int = 5) = List(length) {
        text().toSearchSource()
    }.toSet()

    fun languages(length: Int = 5) = List(length) {
        Language(LangType.ENGLISH)
    }.toSet()

    fun eligibilityCriteria(length: Int = 5) = List(length) {
        Criterion.toInclude(text())
        Criterion.toExclude(text())
    }.toSet()

    fun questionsSet(length: Int = 5) = List(length) { QuestionId(UUID.randomUUID()) }.toSet()
}
