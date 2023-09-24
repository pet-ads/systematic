package br.all.application.question.repository

fun Question.toDto = QuestionDto(
    id.value
)