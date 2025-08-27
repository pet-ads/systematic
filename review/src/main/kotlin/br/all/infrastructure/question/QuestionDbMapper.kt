package br.all.infrastructure.question

import br.all.application.question.repository.QuestionDto

fun QuestionDocument.toDto() = QuestionDto(
    questionId = questionId,
    systematicStudyId = systematicStudyId,
    code = code,
    description = description,
    questionType = questionType,
    scales = scales,
    higher = higher,
    lower = lower,
    options = options,
    context = context
)

fun QuestionDto.toDocument() = QuestionDocument(
    questionId = questionId,
    systematicStudyId = systematicStudyId,
    code = code,
    description = description,
    questionType = questionType,
    scales = scales,
    higher = higher,
    lower = lower,
    options = options,
    context = context
)