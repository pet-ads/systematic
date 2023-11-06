package br.all.domain.model.protocol

import br.all.domain.model.review.ReviewId
import br.all.domain.shared.utils.Language
import br.all.domain.shared.utils.phrase.Phrase
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import java.util.*

class ProtocolTest {
    @Test
    fun `Should successfully create a protocol`() {
        val protocolId = ProtocolId(UUID.randomUUID())
        val reviewId = ReviewId(UUID.randomUUID())
        assertDoesNotThrow {
            Protocol.write().identifiedBy(protocolId, reviewId, setOf("Keyword"))
                .researchesFor(Phrase("Something")).because(Phrase("It is important"))
                .toAnswer(setOf(ResearchQuestion(Phrase("What is the question which its answer is 42?"))))
                .searchProcessWillFollow(Phrase("Reading philosophical articles"), "String")
                .at(setOf(SearchSource("Some Source With Many Philosophical Articles")))
                .selectedBecause(Phrase("I want so"))
                .searchStudiesOf(setOf(Language(Language.LangType.ENGLISH)), Phrase("Primaries and secondaries"))
                .selectionProcessWillFollowAs(Phrase("Classify articles by criteria"))
                .selectStudiesBy(setOf(
                    Criteria(Phrase("It has deep reflexion about life"), Criteria.CriteriaType.INCLUSION),
                    Criteria(Phrase("It does not talk about life!"), Criteria.CriteriaType.EXCLUSION)
                )).collectDataBy(Phrase("Reading the articles and reflect about them"))
                .analyseDataBy(Phrase("Analyse opinions on each article"))
                .finish()
        }
    }

    @Test
    fun `Should throw if the search string is blank`() {
        val protocolId = ProtocolId(UUID.randomUUID())
        val reviewId = ReviewId(UUID.randomUUID())
        assertThrows<IllegalArgumentException> {
            Protocol.write().identifiedBy(protocolId, reviewId, setOf("Keyword"))
                .researchesFor(Phrase("Something")).because(Phrase("It is important"))
                .toAnswer(setOf(ResearchQuestion(Phrase("What is the question which its answer is 42?"))))
                .searchProcessWillFollow(Phrase("Reading philosophical articles"), "")
                .at(setOf(SearchSource("Some Source With Many Philosophical Articles")))
                .selectedBecause(Phrase("I want so"))
                .searchStudiesOf(setOf(Language(Language.LangType.ENGLISH)), Phrase("Primaries and secondaries"))
                .selectionProcessWillFollowAs(Phrase("Classify articles by criteria"))
                .selectStudiesBy(setOf(
                    Criteria(Phrase("It has deep reflexion about life"), Criteria.CriteriaType.INCLUSION),
                    Criteria(Phrase("It does not talk about life!"), Criteria.CriteriaType.EXCLUSION)
                )).collectDataBy(Phrase("Reading the articles and reflect about them"))
                .analyseDataBy(Phrase("Analyse opinions on each article"))
                .finish()
        }
    }

    @Test
    fun `Should throw if there is no inclusion criteria`() {
        val protocolId = ProtocolId(UUID.randomUUID())
        val reviewId = ReviewId(UUID.randomUUID())
        assertThrows<IllegalArgumentException> {
            Protocol.write().identifiedBy(protocolId, reviewId, setOf("Keyword"))
                .researchesFor(Phrase("Something")).because(Phrase("It is important"))
                .toAnswer(setOf(ResearchQuestion(Phrase("What is the question which its answer is 42?"))))
                .searchProcessWillFollow(Phrase("Reading philosophical articles"), "String")
                .at(setOf(SearchSource("Some Source With Many Philosophical Articles")))
                .selectedBecause(Phrase("I want so"))
                .searchStudiesOf(setOf(Language(Language.LangType.ENGLISH)), Phrase("Primaries and secondaries"))
                .selectionProcessWillFollowAs(Phrase("Classify articles by criteria"))
                .selectStudiesBy(setOf(
                    Criteria(Phrase("It does not talk about life!"), Criteria.CriteriaType.EXCLUSION)
                )).collectDataBy(Phrase("Reading the articles and reflect about them"))
                .analyseDataBy(Phrase("Analyse opinions on each article"))
                .finish()
        }
    }
}
