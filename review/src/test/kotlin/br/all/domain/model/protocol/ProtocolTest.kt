package br.all.domain.model.protocol

import br.all.domain.model.review.ReviewId
import br.all.domain.shared.utils.Language
import br.all.domain.shared.utils.Phrase
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import java.util.*

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

    private fun generateProtocol(
        searchString: String = "String",
        criteria: Set<Criteria> = setOf(
            Criteria.toInclude(Phrase("It has deep reflexion about life")),
            Criteria.toExclude(Phrase("It does not talk about life!")),
        )
    ): Protocol {
        val protocolId = ProtocolId(UUID.randomUUID())
        val reviewId = ReviewId(UUID.randomUUID())

        return Protocol.write().identifiedBy(protocolId, reviewId, setOf("Keyword"))
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
