package br.all.protocol.shared

import br.all.domain.shared.utils.paragraph
import br.all.domain.shared.utils.paragraphList
import br.all.infrastructure.protocol.ProtocolDocument
import io.github.serpro69.kfaker.Faker
import java.util.*

class TestDataFactory {
    val protocol: UUID = UUID.randomUUID()

    private val faker = Faker()
    
    fun createProtocolDocument(
        id: UUID = protocol,
        goal: String? = faker.paragraph(5),
        justification: String? = faker.paragraph(5),
        researchQuestions: Set<String> = faker.paragraphList(5, 5).toSet(),
        keywords: Set<String> = faker.paragraphList(5, 5).toSet(),
        searchString: String? = faker.paragraph(5),
        informationSources: Set<String> = faker.paragraphList(5, 5).toSet(),
        sourcesSelectionCriteria: String? = faker.paragraph(5),
        searchMethod: String? = faker.paragraph(5),
        studiesLanguages: Set<String> = faker.paragraphList(5, 5).toSet(),
        studyTypeDefinition: String? = faker.paragraph(5),
        selectionProcess: String? = faker.paragraph(5),
        selectionCriteria: Set<Pair<String, String>> = List(5) { faker.paragraph(5) to faker.paragraph(5) }.toSet(),
        dataCollectionProcess: String? = faker.paragraph(5),
        analysisAndSynthesisProcess: String? = faker.paragraph(5),
        extractionQuestions: Set<UUID> = List(5) { UUID.randomUUID() }.toSet(),
        robQuestions: Set<UUID> = List(5) { UUID.randomUUID() }.toSet(),
        population: String? = null,
        intervention: String? = null,
        control: String? = null,
        outcome: String? = null,
        context: String? = null,
    ) = ProtocolDocument(
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
    
        population,
        intervention,
        control,
        outcome,
        context,
    )
}
