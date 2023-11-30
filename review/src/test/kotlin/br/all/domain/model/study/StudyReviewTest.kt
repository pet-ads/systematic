package br.all.domain.model.study

import br.all.domain.model.review.SystematicStudyId
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertAll
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class StudyReviewTest {

    //private lateinit var Faker

    @BeforeEach
    fun setup() {

    }

    @Test
    fun `should not create systematic review without search source`() {
        assertFailsWith<IllegalArgumentException> {
            StudyReview(
                StudyReviewId(1),
                SystematicStudyId(UUID.randomUUID()),
                studyType = StudyType.ARTICLE,
                title = "title",
                abstract = "abstract",
                year = 2000,
                venue = "ACM Sigdoc",
                authors = "Jao e maria",
                searchSources = mutableSetOf()
            )
        }
    }

    @Test
    fun `should create systematic review with valid input and generate default values`() {
        val studyReview = createValidStudy()
        assertAll(
            { assertEquals(SelectionStatus.UNCLASSIFIED, studyReview.selectionStatus) },
            { assertEquals(ExtractionStatus.UNCLASSIFIED, studyReview.extractionStatus) },
            { assertEquals(ReadingPriority.LOW, studyReview.readingPriority) }
        )
    }

    @Test
    fun `should allow to include unclassified study in selection phase`() {
        val study = createValidStudy()
        study.includeInSelection()
        assertEquals(SelectionStatus.INCLUDED, study.selectionStatus)
    }

    @Test
    fun `should allow to include selected study in extraction phase`() {
        val study = createValidStudy()
        study.includeInSelection()
        study.includeInExtraction()
        assertEquals(ExtractionStatus.INCLUDED, study.extractionStatus)
    }

    @Test
    fun `should throw if tries to include in extraction a study excluded in selection`() {
        val study = createValidStudy()
        study.excludeInSelection()
        assertFailsWith<IllegalStateException> { study.includeInExtraction() }
    }

    @Test
    fun `should change selection status from unclassified to included if included in extraction`() {
        val study = createValidStudy()
        study.includeInExtraction()
        assertAll(
            { assertEquals(SelectionStatus.INCLUDED, study.selectionStatus) },
            { assertEquals(ExtractionStatus.INCLUDED, study.extractionStatus) }
        )
    }

    @Test
    fun `should change selection status from unclassified to excluded if excluded in extraction`() {
        val study = createValidStudy()
        study.excludeInExtraction()
        assertAll(
            { assertEquals(SelectionStatus.EXCLUDED, study.selectionStatus) },
            { assertEquals(ExtractionStatus.EXCLUDED, study.extractionStatus) }
        )
    }

    @Test
    fun `should mark as excluded in both selection and extraction a study excluded in selection`() {
        val study = createValidStudy()
        study.excludeInSelection()
        assertAll(
            { assertEquals(SelectionStatus.EXCLUDED, study.selectionStatus) },
            { assertEquals(ExtractionStatus.EXCLUDED, study.extractionStatus) }
        )
    }

    @Test
    fun `should allow to declassify in selection `() {
        val study = createValidStudy()
        study.includeInSelection()
        study.declassifyInSelection()
        assertEquals(SelectionStatus.UNCLASSIFIED, study.selectionStatus)
    }

    @Test
    fun `should throw if tries to declassify in selection a study classified in extraction`() {
        val study = createValidStudy()
        study.includeInExtraction()
        assertFailsWith<IllegalStateException> { study.declassifyInSelection() }
    }

    @Test
    fun `should allow to declassify study in extraction`() {
        val study = createValidStudy()
        study.includeInSelection()
        study.includeInExtraction()
        study.declassifyInExtraction()
        assertAll(
            { assertEquals(SelectionStatus.INCLUDED, study.selectionStatus) },
            { assertEquals(ExtractionStatus.UNCLASSIFIED, study.extractionStatus) }
        )
    }

    private fun createValidStudy() = StudyReview(
        StudyReviewId(1),
        SystematicStudyId(UUID.randomUUID()),
        studyType = StudyType.INBOOK,
        title = "title",
        abstract = "abstract",
        year = 2000,
        venue = "ACM Sigdoc",
        authors = "Jao e maria",
        searchSources = mutableSetOf("ACM")
    )
}