package br.all.domain.model.protocol

import br.all.domain.model.question.QuestionId
import br.all.domain.model.review.SystematicStudyId
import br.all.domain.shared.valueobject.Language
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import java.util.*
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ProtocolTest {
    @Test
    fun `Should successfully create a protocol`() {
        assertDoesNotThrow {
            generateProtocol()
        }
    }

    @Test
    fun `Should throw if the search string is blank`() {
        assertThrows<IllegalArgumentException> {
            generateProtocol("")
        }
    }

    @Test
    fun `Should throw if there is no inclusion criteria`() {
        assertThrows<IllegalArgumentException> {
            generateProtocol(criteria =  setOf(Criteria.toExclude("It does not talk about life!")))
        }
    }

    @Test
    fun `Should throw if there is no exclusion criteria`() {
        assertThrows<IllegalArgumentException> {
            generateProtocol(criteria =  setOf(Criteria.toInclude("It has deep reflexion about life")))
        }
    }

    @Test
    fun `Should add a new keyword if it is not in the protocol yet`() {
        val sut = generateProtocol()
        val newKeyword = "New keyword"

        sut.addKeyword(newKeyword)

        assertAll(
            { assertEquals(2, sut.keywords.size) },
            { assertContains(sut.keywords, newKeyword) }
        )
    }

    @Test
    fun `Should do nothing when trying to add a keyword that already is in the protocol`() {
        val sut = generateProtocol()
        val keyword = "Keyword"

        assertAll(
            { assertDoesNotThrow { sut.addKeyword(keyword) } },
            { assertEquals(1, sut.keywords.size) }
        )
    }

    @Test
    fun `Should successfully remove a keyword if it is not the last one`() {
        val removingKeyword = "Keyword"
        val sut = generateProtocol(keywords = setOf(removingKeyword, "Other keyword"))

        assertAll(
            { assertDoesNotThrow { sut.removeKeyword(removingKeyword) } },
            { assertEquals(1, sut.keywords.size) },
            { assertTrue { removingKeyword !in sut.keywords } },
        )
    }

    @Test
    fun `Should throw when trying to remove the last keyword`() {
        val removingKeyword = "Keyword"
        val sut = generateProtocol()

        assertThrows<IllegalStateException> { sut.removeKeyword(removingKeyword) }
    }

    @Test
    fun `Should throw when trying to remove an nonexistent keyword`() {
        val sut = generateProtocol()
        assertThrows<NoSuchElementException> { sut.removeKeyword("Nonexistent keyword") }
    }

    @Test
    fun `Should add a information source if it is not in the protocol yet`() {
        val sut = generateProtocol()
        val newSearchSource = SearchSource("New SearchSource")

        sut.addInformationSource(newSearchSource)

        assertAll(
            { assertEquals(2, sut.informationSources.size) },
            { assertContains(sut.informationSources, newSearchSource) }
        )
    }

    @Test
    fun `Should do nothing when trying to add a information source that already is in the protocol`() {
        val sut = generateProtocol()
        val existentInformationSource = SearchSource("SomeSourceWithManyPhilosophicalArticles")

        assertAll(
            { assertDoesNotThrow { sut.addInformationSource(existentInformationSource) } },
            { assertEquals(1, sut.informationSources.size) }
        )
    }
    
    @Test
    fun `Should successfully remove a information source if it is not the last one`() {
        val removingSource = SearchSource("PhilosophicalSource")
        val sut = generateProtocol(sources = setOf(
            removingSource,
            SearchSource("Other Source"),
        ))

        sut.removeInformationSource(removingSource)

        assertAll(
            { assertEquals(1, sut.informationSources.size) },
            { assertTrue { removingSource !in sut.informationSources } },
        )
    }

    @Test
    fun `Should throw when trying to remove the last information source`() {
        val sut = generateProtocol()
        val removingSource = SearchSource("SomeSourceWithManyPhilosophicalArticles")

        assertThrows<IllegalStateException> { sut.removeInformationSource(removingSource) }
    }

    @Test
    fun `Should throw when trying to remove a information source that does not exist`() {
        val sut = generateProtocol()
        val nonexistentSource = SearchSource("Nonexistent Source")

        assertThrows<NoSuchElementException> { sut.removeInformationSource(nonexistentSource) }
    }

    @Test
    fun `Should add a new language if it is not in the protocol`() {
        val sut = generateProtocol()
        val newLanguage = Language(Language.LangType.PORTUGUESE)

        sut.addLanguage(newLanguage)

        assertAll(
            { assertEquals(2, sut.studiesLanguages.size) },
            { assertContains(sut.studiesLanguages, newLanguage) },
        )
    }

    @Test
    fun `Should do nothing when trying to add a language that already is in the protocol`() {
        val sut = generateProtocol()
        val existentLanguage = Language(Language.LangType.ENGLISH)

        assertAll(
            { assertDoesNotThrow { sut.addLanguage(existentLanguage) } },
            { assertEquals(1, sut.studiesLanguages.size) },
        )
    }

    @Test
    fun `Should successfully remove a language if it is not the last one`() {
        val removingLanguage = Language(Language.LangType.PORTUGUESE)
        val sut = generateProtocol(languages = setOf(
            removingLanguage,
            Language(Language.LangType.ENGLISH),
        ))

        sut.removeLanguage(removingLanguage)

        assertAll(
            { assertEquals(1, sut.studiesLanguages.size) },
            { assertTrue { removingLanguage !in sut.studiesLanguages } },
        )
    }

    @Test
    fun `Should throw when trying to remove the last language`() {
        val sut = generateProtocol()
        val removingLanguage = Language(Language.LangType.ENGLISH)

        assertThrows<IllegalStateException> { sut.removeLanguage(removingLanguage) }
    }

    @Test
    fun `Should throw when trying to remove a nonexistent language from protocol`() {
        val sut = generateProtocol()
        val nonexistentLanguage = Language(Language.LangType.PORTUGUESE)

        assertThrows<NoSuchElementException> { sut.removeLanguage(nonexistentLanguage) }
    }
    
    @Test
    fun `Should add a new criteria if it is not in the protocol`() {
        val sut = generateProtocol()
        val newCriteria = Criteria.toInclude("Nice thoughts")

        sut.addSelectionCriteria(newCriteria)

        assertAll(
            { assertEquals(3, sut.selectionCriteria.size) },
            { assertContains(sut.selectionCriteria, newCriteria) },
        )
    }

    @Test
    fun `Should do nothing when trying to add a repeated criteria`() {
        val sut = generateProtocol()
        val repeatedCriteria = Criteria.toInclude("It has deep reflection about life")

        assertAll(
            { assertDoesNotThrow { sut.addSelectionCriteria(repeatedCriteria) } },
            { assertEquals(2, sut.selectionCriteria.size) },
        )
    }

    @ParameterizedTest
    @CsvSource("Nice thoughts,INCLUSION", "Bad thoughts,EXCLUSION")
    fun `Should successfully remove a criteria if it is not the last of such type`(
        description: String,
        type: Criteria.CriteriaType,
    ) {
        val sut = generateProtocol(criteria = setOf(
            Criteria.toInclude("It has deep reflection about life!"),
            Criteria.toInclude("Nice thoughts"),
            Criteria.toExclude("It does not talk about life"),
            Criteria.toExclude("Bad thoughts"),
        ))
        val removingCriteria = Criteria(description, type)

        assertAll(
            { assertDoesNotThrow { sut.removeSelectionCriteria(removingCriteria) } },
            { assertEquals(3, sut.selectionCriteria.size) },
        )
    }

    @ParameterizedTest
    @CsvSource("It has deep reflection about life,INCLUSION", "It does not talk about life!,EXCLUSION")
    fun `Should throw when trying to remove the last criteria o a type`(
        description: String, type: Criteria.CriteriaType) {

        val sut = generateProtocol()
        val removingCriteria = Criteria(description, type)

        assertThrows<IllegalStateException> { sut.removeSelectionCriteria(removingCriteria) }
    }

    @Test
    fun `Should throw when trying to remove a criteria that is not defined in the protocol`() {
        val sut = generateProtocol()
        val nonexistentCriteria = Criteria.toInclude("Nice thoughts")

        assertThrows<NoSuchElementException> { sut.removeSelectionCriteria(nonexistentCriteria) }
    }
    
    @Test
    fun `Should add a new extraction question if it is not defined yet`() {
        val sut = generateProtocol()
        val newExtractionQuestion = QuestionId(UUID.randomUUID())

        sut.addExtractionQuestion(newExtractionQuestion)

        assertAll(
            { assertEquals(1, sut.extractionQuestions.size) },
            { assertContains(sut.extractionQuestions, newExtractionQuestion) },
        )
    }

    @Test
    fun `Should do nothing when trying to add a extraction question that has already been defined`() {
        val question = QuestionId(UUID.randomUUID())
        val sut = generateProtocol(extractionQuestions = setOf(question))

        assertAll(
            { assertDoesNotThrow { sut.addExtractionQuestion(question) } },
            { assertEquals(1, sut.extractionQuestions.size) },
        )
    }

    @Test
    fun `Should remove a extraction question if its present`() {
        val removingQuestion = QuestionId(UUID.randomUUID())
        val sut = generateProtocol(extractionQuestions = setOf(removingQuestion))

        sut.removeExtractionQuestion(removingQuestion)

        assertEquals(0, sut.extractionQuestions.size)
    }
    
    @Test
    fun `Should throw when trying to remove a question that does not belongs to the protocol`() {
        val sut = generateProtocol()
        val removingQuestion = QuestionId(UUID.randomUUID())

        assertThrows<NoSuchElementException> { sut.removeExtractionQuestion(removingQuestion) }
    }

    @Test
    fun `Should a new rob question successfully if it is not defined`() {
        val sut = generateProtocol()
        val newRobQuestion = QuestionId(UUID.randomUUID())

        sut.addRobQuestion(newRobQuestion)

        assertAll(
            { assertEquals(1, sut.robQuestions.size) },
            { assertContains(sut.robQuestions, newRobQuestion) },
        )
    }

    @Test
    fun `Should do nothing when trying to add repeated rob questions`() {
        val repeatedQuestion = QuestionId(UUID.randomUUID())
        val sut = generateProtocol(robQuestions = setOf(repeatedQuestion))

        assertAll(
            { assertDoesNotThrow { sut.addRobQuestion(repeatedQuestion) } },
            { assertEquals(1, sut.robQuestions.size) },
        )
    }

    @Test
    fun `Should existent rob questions be successfully removed`() {
        val robQuestion = QuestionId(UUID.randomUUID())
        val sut = generateProtocol(robQuestions = setOf(robQuestion))

        sut.removeRobQuestion(robQuestion)

        assertEquals(0, sut.robQuestions.size)
    }

    @Test
    fun `Should throw when trying to remove nonexistent rob questions`() {
        val sut = generateProtocol()
        val removingQuestion = QuestionId(UUID.randomUUID())

        assertThrows<NoSuchElementException> { sut.removeRobQuestion(removingQuestion) }
    }

    private fun generateProtocol(
        searchString: String = "String",
        criteria: Set<Criteria> = setOf(
            Criteria.toInclude("It has deep reflection about life"),
            Criteria.toExclude("It does not talk about life!"),
        ),
        keywords: Set<String> = setOf("Keyword"),
        sources: Set<SearchSource> = setOf(SearchSource("SomeSourceWithManyPhilosophicalArticles")),
        languages: Set<Language> = setOf(Language(Language.LangType.ENGLISH)),
        extractionQuestions: Set<QuestionId> = emptySet(),
        robQuestions: Set<QuestionId> = emptySet(),
    ): Protocol {
        val systematicStudyId = SystematicStudyId(UUID.randomUUID())

        return Protocol.with(systematicStudyId, keywords)
            .researchesFor("Something")
            .because("It is important")
            .toAnswer(setOf(ResearchQuestion("What is the question which its answer is 42?")))
            .followingSearchProcess("Reading philosophical articles", searchString)
            .inSearchSources(sources)
            .selectedBecause("I want so")
            .searchingStudiesIn(languages, "Primaries and secondaries")
            .followingSelectionProcess("Classify articles by criteria")
            .withElegibilityCriteria(criteria)
            .followingDataCollectionProcess("Reading the articles and reflect about them")
            .followingSynthesisProcess("Analyse opinions on each article")
            .extractDataByAnswering(extractionQuestions)
            .qualityFormConsiders(robQuestions)
            .build()
    }
}
