package br.all.domain.model.study

import br.all.domain.shared.ddd.Entity
import br.all.domain.model.review.SystematicStudyId
import java.util.*

class StudyReview(
    val studyId: StudyReviewId,
    val systematicStudyId: SystematicStudyId,
    val studyType: StudyType,
    val title: String,
    val year: Int,
    val authors: String,
    val venue: String,
    val abstract: String,
    val keywords: Set<String> = emptySet(),
    val searchSources: MutableSet<String>,
    val references: List<String> = emptyList(),
    val doi: Doi? = null,
    val criteria: MutableSet<String> = mutableSetOf(),
    val formAnswers: MutableMap<UUID, Answer<*>> = mutableMapOf(),
    val qualityAnswers: MutableMap<UUID, Answer<*>> = mutableMapOf(),
    var comments: String = "",
    var readingPriority: ReadingPriority = ReadingPriority.LOW,
    selectionStatus: SelectionStatus = SelectionStatus.UNCLASSIFIED,
    extractionStatus: ExtractionStatus = ExtractionStatus.UNCLASSIFIED,
) : Entity(studyId) {

    private val study: Study
    var selectionStatus: SelectionStatus = selectionStatus
        private set
    var extractionStatus: ExtractionStatus = extractionStatus
        private set

    init {
        require(searchSources.size > 0) { "The study must be related to at least one search source." }
        study = Study(studyType, title, year, authors, venue, abstract, keywords, references, doi)
    }

    companion object

    fun addSearchSource(searchSource: String) = searchSources.add(searchSource)

    fun removeSearchSource(searchSource: String) =
        if (searchSource.length > 1) searchSources.remove(searchSource)
        else throw IllegalStateException("The study must be related to at least one search source.")

    fun addCriterion(criterion: String) = criteria.add(criterion)

    fun removeCriterion(criterion: String) = criteria.remove(criterion)

    fun answerQualityQuestionOf(questionId: UUID, answer: Answer<*>) = qualityAnswers.put(questionId, answer)

    fun answerFormQuestionOf(questionId: UUID, answer: Answer<*>) = formAnswers.put(questionId, answer)

    fun includeInSelection() = apply { selectionStatus = SelectionStatus.INCLUDED }

    fun excludeInSelection() {
        selectionStatus = SelectionStatus.EXCLUDED
        if (shouldNotConsiderForExtraction()) extractionStatus = ExtractionStatus.EXCLUDED
    }

    fun declassifyInSelection() {
        if(extractionStatus != ExtractionStatus.UNCLASSIFIED)
            throw IllegalStateException("A study classified in extraction can not be declassified in selection.")
        selectionStatus = SelectionStatus.UNCLASSIFIED
    }

    private fun shouldNotConsiderForExtraction() =
        extractionStatus == ExtractionStatus.INCLUDED || extractionStatus == ExtractionStatus.UNCLASSIFIED

    fun includeInExtraction() {
        if (selectionStatus == SelectionStatus.EXCLUDED)
            throw IllegalStateException("A study excluded in selection can not be included in extraction.")
        if (selectionStatus == SelectionStatus.UNCLASSIFIED)
            selectionStatus = SelectionStatus.INCLUDED
        extractionStatus = ExtractionStatus.INCLUDED
    }

    fun excludeInExtraction() {
        if (selectionStatus == SelectionStatus.UNCLASSIFIED)
            selectionStatus = SelectionStatus.EXCLUDED
        extractionStatus = ExtractionStatus.EXCLUDED
    }

    fun declassifyInExtraction() = apply { extractionStatus = ExtractionStatus.UNCLASSIFIED }

    fun markAsDuplicated(){
        selectionStatus = SelectionStatus.DUPLICATED
        extractionStatus = ExtractionStatus.DUPLICATED
    }

    override fun toString(): String {
        return "StudyReview(reviewId=$systematicStudyId, searchSources=$searchSources, criteria=$criteria, " +
                "formAnswers=$formAnswers, qualityAnswers=$qualityAnswers, comments='$comments', " +
                "readingPriority=$readingPriority, extractionStatus=$extractionStatus, " +
                "selectionStatus=$selectionStatus, study=$study)"
    }
}