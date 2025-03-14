package br.all.application.study.repository

import br.all.application.study.create.CreateStudyReviewService.RequestModel
import br.all.application.study.update.interfaces.UpdateStudyReviewService
import br.all.domain.model.protocol.Criterion
import br.all.domain.model.protocol.Criterion.CriterionType
import br.all.domain.model.review.SystematicStudyId
import br.all.domain.model.search.SearchSessionID
import br.all.domain.model.study.*

fun StudyReview.toDto() = StudyReviewDto(
    id.value(),
    systematicStudyId.value(),
    searchSessionId.value(),
    studyType.toString(),
    title,
    year,
    authors,
    venue,
    abstract,
    keywords,
    references,
    doi?.toString(),
    searchSources,
    criteria.map { it.description }.toSet(),
    formAnswers.associate { it.questionId to it.value.toString() },
    robAnswers.associate { it.questionId to it.value.toString() },
    comments,
    readingPriority.toString(),
    extractionStatus.toString(),
    selectionStatus.toString()
)


fun StudyReview.Companion.fromDto(dto: StudyReviewDto) = StudyReview(
    StudyReviewId(dto.studyReviewId),
    SystematicStudyId(dto.systematicStudyId),
    SearchSessionID(dto.searchSessionId),
    StudyType.valueOf(dto.studyType),
    dto.title,
    dto.year,
    dto.authors,
    dto.venue,
    dto.abstract,
    dto.doi?.let { Doi(it) },
    dto.keywords.toMutableSet(),
    dto.searchSources.toMutableSet(),
    dto.references.toMutableList(),
    dto.criteria.map { Criterion.toInclude(it) }.toMutableSet(),
    dto.formAnswers.map { (questionId, answer) -> Answer(questionId, answer) }.toMutableSet(),
    dto.robAnswers.map { (questionId, answer) -> Answer(questionId, answer) }.toMutableSet(),
    dto.comments,
    ReadingPriority.valueOf(dto.readingPriority),
    SelectionStatus.valueOf(dto.selectionStatus),
    ExtractionStatus.valueOf(dto.extractionStatus)
)


fun StudyReview.Companion.fromStudyRequestModel(studyId: Long, request: RequestModel) = StudyReview(
    StudyReviewId(studyId),
    SystematicStudyId(request.systematicStudyId),
    SearchSessionID(request.searchSessionId),
    StudyType.valueOf(request.type.uppercase()),
    request.title,
    request.year,
    request.authors,
    request.venue,
    request.abstract,
    keywords = request.keywords,
    searchSources = mutableSetOf(request.source)
)

fun StudyReview.Companion.fromStudyUpdateRequestModel(studyId: Long, request: UpdateStudyReviewService.RequestModel) = StudyReview(
    StudyReviewId(studyId),
    SystematicStudyId(request.systematicStudyId),
    SearchSessionID(request.searchSessionId),
    StudyType.valueOf(request.type.uppercase()),
    request.title,
    request.year,
    request.authors,
    request.venue,
    request.abstract,
    keywords = request.keywords,
    searchSources = mutableSetOf(request.source)
)
