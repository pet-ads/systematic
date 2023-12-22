package br.all.infrastructure.question

import br.all.application.question.create.QuestionDTO

fun QuestionDocument.toDto() = QuestionDTO(
    systematicStudyId,
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

fun QuestionDTO.toDocument() = QuestionDocument(
    questionId,
    systematicStudyId,
    protocolId,
    code,
    description,
    questionType,
    scales,
    higher,
    lower,
    options
)