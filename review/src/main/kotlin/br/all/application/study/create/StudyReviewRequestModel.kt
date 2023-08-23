package br.all.application.study.create

data class StudyReviewRequestModel (
    val title: String,
    val year: Int,
    val authors: String,
    val venue: String,
    val abstract: String,
    val keywords: Set<String>,
    val source: String,
)
