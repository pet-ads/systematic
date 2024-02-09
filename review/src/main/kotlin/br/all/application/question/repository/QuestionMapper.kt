package br.all.application.question.repository

import br.all.application.question.create.CreateQuestionService.QuestionType
import br.all.domain.model.question.LabeledScale
import br.all.domain.model.question.NumberScale
import br.all.domain.model.question.PickList
import br.all.domain.model.question.Question

fun Question<*>.toDto(type: QuestionType) = QuestionDto(
    this.id.value(),
    this.systematicStudyId.value,
    this.code,
    this.description,
    type.toString(),
    (this as? LabeledScale)?.scales?.mapValues { it.value.value },
    (this as? NumberScale)?.lower,
    (this as? NumberScale)?.higher,
    (this as? PickList)?.options
)

