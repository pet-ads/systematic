package br.all.domain.model.protocol

import br.all.domain.model.review.ReviewId
import br.all.domain.shared.utils.Language
import br.all.domain.shared.utils.Phrase
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
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
        val protocol = generateProtocol()
        val newKeyword = "New keyword"

        protocol.addKeyword(newKeyword)

        assertAll(
            { assertEquals(2, protocol.keywords.size) },
            { assertContains(protocol.keywords, newKeyword) }
        )
    }

    @Test
    fun `Should do nothing when trying to add a keyword that already is in the protocol`() {
        val protocol = generateProtocol()
        val keyword = "Keyword"

        assertAll(
            { assertDoesNotThrow { protocol.addKeyword(keyword) } },
            { assertEquals(1, protocol.keywords.size) }
        )
    }

    @Test
    fun `Should successfully remove a keyword if it is not the last one`() {
        val removingKeyword = "Keyword"
        val protocol = generateProtocol(keywords = setOf(removingKeyword, "Other keyword"))

        assertAll(
            { assertDoesNotThrow { protocol.removeKeyword(removingKeyword) } },
            { assertEquals(1, protocol.keywords.size) },
            { assertTrue { removingKeyword !in protocol.keywords } },
        )
    }

    private fun generateProtocol(
        searchString: String = "String",
        criteria: Set<Criteria> = setOf(
            Criteria.toInclude(Phrase("It has deep reflexion about life")),
            Criteria.toExclude(Phrase("It does not talk about life!")),
        ),
        keywords: Set<String> = setOf("Keyword"),
    ): Protocol {
        val protocolId = ProtocolId(UUID.randomUUID())
        val reviewId = ReviewId(UUID.randomUUID())

        return Protocol.write().identifiedBy(protocolId, reviewId, keywords)
            .researchesFor(Phrase("Something")).because(Phrase("It is important"))
            .toAnswer(setOf(ResearchQuestion(Phrase("What is the question which its answer is 42?"))))
            .searchProcessWillFollow(Phrase("Reading philosophical articles"), searchString)
            .at(setOf(SearchSource("Some Source With Many Philosophical Articles")))
            .selectedBecause(Phrase("I want so"))
            .searchStudiesOf(setOf(Language(Language.LangType.ENGLISH)), Phrase("Primaries and secondaries"))
            .selectionProcessWillFollowAs(Phrase("Classify articles by criteria"))
            .selectStudiesBy(criteria)
            .collectDataBy(Phrase("Reading the articles and reflect about them"))
            .analyseDataBy(Phrase("Analyse opinions on each article"))
            .finish()
    }
}
