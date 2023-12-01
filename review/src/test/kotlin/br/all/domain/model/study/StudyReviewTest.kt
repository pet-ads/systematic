package br.all.domain.model.study

import br.all.domain.model.protocol.Criteria
import br.all.domain.model.review.SystematicStudyId
import br.all.domain.shared.utils.Phrase
import io.github.serpro69.kfaker.Faker
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertAll
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class StudyReviewTest {

    private val faker = Faker()

    @Test
    fun `should not create systematic review without search source`() {
        assertFailsWith<IllegalArgumentException> { createStudy(searchSources = mutableSetOf()) }
    }

    @Test
    fun `should create systematic review with valid input and generate default values`() {
        val studyReview = createStudy()
        assertAll(
            { assertEquals(SelectionStatus.UNCLASSIFIED, studyReview.selectionStatus) },
            { assertEquals(ExtractionStatus.UNCLASSIFIED, studyReview.extractionStatus) },
            { assertEquals(ReadingPriority.LOW, studyReview.readingPriority) }
        )
    }

    @Test
    fun `should allow to include unclassified study in selection phase`() {
        val study = createStudy()
        study.includeInSelection()
        assertEquals(SelectionStatus.INCLUDED, study.selectionStatus)
    }

    @Test
    fun `should allow to include selected study in extraction phase`() {
        val study = createStudy()
        study.includeInSelection()
        study.includeInExtraction()
        assertEquals(ExtractionStatus.INCLUDED, study.extractionStatus)
    }

    @Test
    fun `should throw if tries to include in extraction a study excluded in selection`() {
        val study = createStudy()
        study.excludeInSelection()
        assertFailsWith<IllegalStateException> { study.includeInExtraction() }
    }

    @Test
    fun `should change selection status from unclassified to included if included in extraction`() {
        val study = createStudy()
        study.includeInExtraction()
        assertAll(
            { assertEquals(SelectionStatus.INCLUDED, study.selectionStatus) },
            { assertEquals(ExtractionStatus.INCLUDED, study.extractionStatus) }
        )
    }

    @Test
    fun `should change selection status from unclassified to excluded if excluded in extraction`() {
        val study = createStudy()
        study.excludeInExtraction()
        assertAll(
            { assertEquals(SelectionStatus.EXCLUDED, study.selectionStatus) },
            { assertEquals(ExtractionStatus.EXCLUDED, study.extractionStatus) }
        )
    }

    @Test
    fun `should mark as excluded in both selection and extraction a study excluded in selection`() {
        val study = createStudy()
        study.excludeInSelection()
        assertAll(
            { assertEquals(SelectionStatus.EXCLUDED, study.selectionStatus) },
            { assertEquals(ExtractionStatus.EXCLUDED, study.extractionStatus) }
        )
    }

    @Test
    fun `should allow to declassify in selection `() {
        val study = createStudy()
        study.includeInSelection()
        study.declassifyInSelection()
        assertEquals(SelectionStatus.UNCLASSIFIED, study.selectionStatus)
    }

    @Test
    fun `should throw if tries to declassify in selection a study classified in extraction`() {
        val study = createStudy()
        study.includeInExtraction()
        assertFailsWith<IllegalStateException> { study.declassifyInSelection() }
    }

    @Test
    fun `should allow to declassify study in extraction`() {
        val study = createStudy()
        study.includeInSelection()
        study.includeInExtraction()
        study.declassifyInExtraction()
        assertAll(
            { assertEquals(SelectionStatus.INCLUDED, study.selectionStatus) },
            { assertEquals(ExtractionStatus.UNCLASSIFIED, study.extractionStatus) }
        )
    }

    @Test
    fun `should both duplicated studies have the same search sources`() {
        val sourceA = faker.book.publisher()
        val sourceB = faker.book.publisher()
        val duplicated = createStudy(searchSources = mutableSetOf(sourceA))
        val duplicateReference = createStudy(searchSources = mutableSetOf(sourceB))
        duplicated.markAsDuplicated(duplicateReference)
        assertAll(
            { assertTrue { duplicated.searchSources.containsAll(listOf(sourceA, sourceB)) } },
            { assertEquals(duplicated.searchSources, duplicateReference.searchSources) },
            { assertEquals(SelectionStatus.DUPLICATED, duplicated.selectionStatus) },
            { assertEquals(ExtractionStatus.DUPLICATED, duplicated.extractionStatus) },
        )
    }

    @Test
    fun `should add eligibility criterion`(){
        val study = createStudy()
        val criterion = Criteria(Phrase(faker.quote.yoda()), Criteria.CriteriaType.INCLUSION)
        study.addCriterion(criterion)
        assertTrue {  study.criteria.contains(criterion) }
    }

    private fun createStudy(
        studyReviewId: Long = 1L,
        systematicStudyId: UUID = UUID.randomUUID(),
        studyType: StudyType = StudyType.INBOOK,
        title: String = faker.book.title(),
        abstract: String = List(faker.random.nextInt(0,20)) { Faker().lorem.words() }.joinToString(" "),
        year: Int = faker.random.nextInt(1900,2030),
        venue: String = faker.book.publisher(),
        authors: String = faker.book.author(),
        searchSources: MutableSet<String> = mutableSetOf(faker.book.publisher())
    ) = StudyReview(
        StudyReviewId(studyReviewId), SystematicStudyId(systematicStudyId),
        studyType, title, year, abstract, venue, authors, searchSources = searchSources
    )
}