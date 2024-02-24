package br.all.application.protocol.util

import br.all.domain.shared.utils.wordsList
import io.github.serpro69.kfaker.Faker
import java.util.*
import br.all.application.protocol.create.CreateProtocolService.RequestModel as CreateRequestModel

class TestDataFactory {
    val researcher: UUID = UUID.randomUUID()
    val systematicStudy: UUID = UUID.randomUUID()

    private val faker = Faker()

    fun createRequestModel(
        researcher: UUID = this.researcher,
        systematicStudy: UUID = this.systematicStudy,
        goal: String? = text(),
        justification: String? = text(),
        keywords: Set<String> = emptySet(),

        searchString: String? = null,
        informationSources: Set<String> = emptySet(),
        sourcesSelectionCriteria: String? = null,
        searchMethod: String? = null,

        studiesLanguages: Set<String> = emptySet(),
        studyTypeDefinition: String? = null,

        selectionProcess: String? = null,
        dataCollectionProcess: String? = null,
        analysisAndSynthesisProcess: String? = null,
    ) = CreateRequestModel(
        researcher,
        systematicStudy,

        goal,
        justification,
        keywords,

        searchString,
        informationSources,
        sourcesSelectionCriteria,
        searchMethod,

        studiesLanguages,
        studyTypeDefinition,

        selectionProcess,
        dataCollectionProcess,
        analysisAndSynthesisProcess,
    )

    fun text() = faker.wordsList(minSize = 1, maxSize = 5).joinToString(" ")

    operator fun component1() = researcher

    operator fun component2() = systematicStudy
}
