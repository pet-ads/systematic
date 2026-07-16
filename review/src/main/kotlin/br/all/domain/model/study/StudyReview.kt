package br.all.domain.model.study

import br.all.domain.shared.ddd.Entity
import br.all.domain.model.protocol.Criterion
import br.all.domain.model.review.SystematicStudyId
import br.all.domain.model.search.SearchSessionID

class StudyReview(
    studyId: StudyReviewId,
    val systematicStudyId: SystematicStudyId,
    val searchSessionId: SearchSessionID,
    val studyType: StudyType,
    val title: String,
    val year: Int,
    val authors: String,
    val venue: String,
    val abstract: String? = null,
    val doi: Doi? = null,
    keywords: Set<String> = mutableSetOf(),
    searchSources: MutableSet<String>,
    references: List<String> = mutableListOf(),
    selectionCriteria: MutableSet<Criterion> = mutableSetOf(),
    extractionCriteria: MutableSet<Criterion> = mutableSetOf(),
    formAnswers: MutableSet<Answer<*>> = mutableSetOf(),
    robAnswers: MutableSet<Answer<*>> = mutableSetOf(),
    var comments: String = "",
    var readingPriority: ReadingPriority = ReadingPriority.LOW,
    selectionStatus: SelectionStatus = SelectionStatus.UNCLASSIFIED,
    extractionStatus: ExtractionStatus = ExtractionStatus.UNCLASSIFIED,
    var score: Int = 0
) : Entity<Long>(studyId) {

    private val study: Study

    private val _keywords: Set<String> = keywords
    val keywords get() = _keywords.toSet()

    private val _searchSources = searchSources
    val searchSources get() = _searchSources.toSet()

    private val _references = references
    val references get() = _references.toList()

    private val _selectionCriteria = selectionCriteria
    private val _extractionCriteria = extractionCriteria

    val selectionCriteria
        get() = _selectionCriteria.toSet()

    val extractionCriteria
        get() = _extractionCriteria.toSet()

    private val _formAnswers = formAnswers
    val formAnswers get() = _formAnswers.toSet()

    private val _qualityAnswers = robAnswers
    val robAnswers get() = _qualityAnswers.toSet()

    var selectionStatus: SelectionStatus = selectionStatus
        private set
    var extractionStatus: ExtractionStatus = extractionStatus
        private set

    init {
        require(searchSources.size > 0) { "The study must be related to at least one search source." }
        study = Study(studyType, title, year, authors, venue, abstract, keywords, references, doi)
    }

    companion object{}

    fun addSelectionCriterion(criterion: Criterion) =
        _selectionCriteria.add(criterion)

    fun removeSelectionCriterion(criterion: Criterion) =
        _selectionCriteria.remove(criterion)


    fun addExtractionCriterion(criterion: Criterion) =
        _extractionCriteria.add(criterion)

    fun removeExtractionCriterion(criterion: Criterion) {
        _extractionCriteria.remove(criterion)
        if (_selectionCriteria.isEmpty()) {
            selectionStatus = SelectionStatus.UNCLASSIFIED
            extractionStatus = ExtractionStatus.UNCLASSIFIED
            _extractionCriteria.clear()
        }
    }

    fun answerQualityQuestionOf( answer: Answer<*>) = _qualityAnswers.add(answer)

    fun answerFormQuestionOf(answer: Answer<*>) = _formAnswers.add(answer)

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

    private fun clearExtractionCriteria() {
        _extractionCriteria.clear()
    }

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
        clearExtractionCriteria()
        extractionStatus = ExtractionStatus.EXCLUDED
    }

    fun declassifyInExtraction() = apply { extractionStatus = ExtractionStatus.UNCLASSIFIED }

    private fun mergeSearchSources(duplicates: List<StudyReview>): MutableSet<String> {
        val unionSources = _searchSources.toMutableSet()
        duplicates.forEach { unionSources.addAll(it.searchSources) }
        return unionSources
    }

    private fun applyMergedSearchSources(unionSources: MutableSet<String>) {
        _searchSources.clear()
        _searchSources.addAll(unionSources)
    }

    private fun mergeDuplicateInformation(duplicates: List<StudyReview>) {
        val unionSources = mergeSearchSources(duplicates)
        applyMergedSearchSources(unionSources)
    }

    fun markAsDuplicatedInSelection(duplicates: List<StudyReview>) {
        mergeDuplicateInformation(duplicates)

        duplicates.forEach { duplicate ->
            duplicate.clearExtractionCriteria()
            duplicate.selectionStatus = SelectionStatus.DUPLICATED
            duplicate.extractionStatus = ExtractionStatus.DUPLICATED
        }
    }

    fun markAsDuplicatedInExtraction(duplicates: List<StudyReview>) {
        mergeDuplicateInformation(duplicates)

        duplicates.forEach { duplicate ->
            duplicate.extractionStatus = ExtractionStatus.DUPLICATED
        }
    }


    override fun toString(): String {
        return "StudyReview(reviewId=$systematicStudyId, searchSources=$searchSources, " +
                "selectionCriteria=$selectionCriteria, extractionCriteria=$extractionCriteria, " +
                "formAnswers=$formAnswers, qualityAnswers=$robAnswers, comments='$comments', " +
                "readingPriority=$readingPriority, extractionStatus=$extractionStatus, " +
                "selectionStatus=$selectionStatus, study=$study)"
    }
}