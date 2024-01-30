package br.all.infrastructure.question

import br.all.application.question.repository.QuestionDto

fun QuestionDocument.toDto() = QuestionDto(
    id,
    protocolId,
    code,
    description,
    questionType,
    scales,
    higher,
    lower,
    options
)

fun QuestionDto.toDocument() = QuestionDocument(
    questionId,
    protocolId,
    code,
    description,
    questionType,
    scales,
    higher,
    lower,
    options
)