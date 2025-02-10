package br.all.application.question.repository

import java.util.*

data class QuestionDto(
    val questionId: UUID,
    val systematicStudyId: UUID,
    val code: String,
    val description: String,
    val questionType: String,
    val scales: Map<String, Int>? = null,
    val higher: Int? = null,
    val lower: Int? = null,
    val options: List<String>? = null,
    val context: String
)