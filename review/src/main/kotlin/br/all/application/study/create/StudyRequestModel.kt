package br.all.application.study.create

import java.util.UUID

data class StudyRequestModel (
    val id: Long,
    val reviewId: UUID,
    val title: String,
    val year: Int,
    val authors: String,
    val venue: String,
    val abstract: String,
    val keywords: Set<String>,
    val source: String,
)
