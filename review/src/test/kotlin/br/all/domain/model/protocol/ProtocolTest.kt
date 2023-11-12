package br.all.domain.model.protocol

import br.all.domain.model.review.ReviewId
import br.all.domain.shared.utils.Language
import br.all.domain.shared.utils.Phrase
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
            generateProtocol(criteria =  setOf(Criteria.toExclude(Phrase("It does not talk about life!"))))
        }
    }

    @Test
    fun `Should throw if there is no exclusion criteria`() {
        assertThrows<IllegalArgumentException> {
            generateProtocol(criteria =  setOf(Criteria.toInclude(Phrase("It has deep reflexion about life"))))
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
        val newCriteria = Criteria.toInclude(Phrase("Nice thoughts"))

        sut.addSelectionCriteria(newCriteria)

        assertAll(
            { assertEquals(3, sut.selectionCriteria.size) },
            { assertContains(sut.selectionCriteria, newCriteria) },
        )
    }

    @Test
    fun `Should do nothing when trying to add a repeated criteria`() {
        val sut = generateProtocol()
        val repeatedCriteria = Criteria.toInclude(Phrase("It has deep reflection about life"))

        assertAll(
            { assertDoesNotThrow { sut.addSelectionCriteria(repeatedCriteria) } },
            { assertEquals(2, sut.selectionCriteria.size) },
        )
    }

    @ParameterizedTest
    @CsvSource("Nice thoughts,INCLUSION", "Bad thoughts,EXCLUSION")
    fun `Should successfully remove a criteria if it is not the last of such type`(
        description: Phrase,
        type: Criteria.CriteriaType,
    ) {
        val sut = generateProtocol(criteria = setOf(
            Criteria.toInclude(Phrase("It has deep reflection about life!")),
            Criteria.toInclude(Phrase("Nice thoughts")),
            Criteria.toExclude(Phrase("It does not talk about life")),
            Criteria.toExclude(Phrase("Bad thoughts")),
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
        description: Phrase,
        type: Criteria.CriteriaType
    ) {
        val sut = generateProtocol()
        val removingCriteria = Criteria(description, type)

        assertThrows<IllegalStateException> { sut.removeSelectionCriteria(removingCriteria) }
    }

    @Test
    fun `Should throw when trying to remove a criteria that is not defined in the protocol`() {
        val sut = generateProtocol()
        val nonexistentCriteria = Criteria.toInclude(Phrase("Nice thoughts"))

        assertThrows<NoSuchElementException> { sut.removeSelectionCriteria(nonexistentCriteria) }
    }
    
    @Test
    fun `Should add a new extraction question if it is not defined yet`() {
        val sut = generateProtocol()
        val newExtractionQuestion = QuestionId(10)

        sut.addExtractionField(newExtractionQuestion)

        assertAll(
            { assertEquals(1, sut.extractionQuestions.size) },
            { assertContains(sut.extractionQuestions, newExtractionQuestion) },
        )
    }

    private fun generateProtocol(
        searchString: String = "String",
        criteria: Set<Criteria> = setOf(
            Criteria.toInclude(Phrase("It has deep reflection about life")),
            Criteria.toExclude(Phrase("It does not talk about life!")),
        ),
        keywords: Set<String> = setOf("Keyword"),
        sources: Set<SearchSource> = setOf(SearchSource("SomeSourceWithManyPhilosophicalArticles")),
        languages: Set<Language> = setOf(Language(Language.LangType.ENGLISH)),
    ): Protocol {
        val protocolId = ProtocolId(UUID.randomUUID())
        val reviewId = ReviewId(UUID.randomUUID())

        return Protocol.write().identifiedBy(protocolId, reviewId, keywords)
            .researchesFor(Phrase("Something")).because(Phrase("It is important"))
            .toAnswer(setOf(ResearchQuestion(Phrase("What is the question which its answer is 42?"))))
            .searchProcessWillFollow(Phrase("Reading philosophical articles"), searchString)
            .at(sources)
            .selectedBecause(Phrase("I want so"))
            .searchStudiesOf(languages, Phrase("Primaries and secondaries"))
            .selectionProcessWillFollowAs(Phrase("Classify articles by criteria"))
            .selectStudiesBy(criteria)
            .collectDataBy(Phrase("Reading the articles and reflect about them"))
            .analyseDataBy(Phrase("Analyse opinions on each article"))
            .finish()
    }
}
