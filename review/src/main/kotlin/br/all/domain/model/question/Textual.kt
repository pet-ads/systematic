package br.all.domain.model.question

import br.all.domain.model.review.SystematicStudyId
import br.all.domain.model.study.Answer

class Textual(
    id: QuestionId,
    systematicStudyId: SystematicStudyId,
    code: String,
    description: String
) : Question<String>(id, systematicStudyId, code, description) {

    override fun answer(value: String): Answer<String> {
        if (value.isBlank()) throw IllegalArgumentException("Answer must not be blank.")
        return Answer(id.value(), value)
    }
}