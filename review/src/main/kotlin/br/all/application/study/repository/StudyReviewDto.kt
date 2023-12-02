package br.all.application.study.repository

import java.util.*

data class StudyReviewDto(
    val systematicStudyId: UUID,
    val studyReviewId: Long,
    val studyType: String,
    val title: String,
    val year: Int,
    val authors: String,
    val venue: String,
    val abstract: String,
    val keywords: Set<String>,
    val references: List<String>,
    val doi: String?,
    val searchSources: Set<String>,
    val criteria: Map<String, String>,
    val formAnswers: Map<UUID, String>,
    val robAnswers: Map<UUID, String>,
    val comments: String,
    val readingPriority: String,
    val extractionStatus: String,
    val selectionStatus: String,
)

