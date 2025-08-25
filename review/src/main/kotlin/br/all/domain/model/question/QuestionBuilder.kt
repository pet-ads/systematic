package br.all.domain.model.question

import br.all.domain.model.review.SystematicStudyId

class QuestionBuilder private constructor(
    private var questionId: QuestionId,
    private var systematicStudyId: SystematicStudyId,
    private var code: String,
    private var description: String,
) {
    companion object {
        fun with(
            id: QuestionId,
            systematicStudyId: SystematicStudyId,
            code: String,
            description: String,
        ) = QuestionBuilder(id, systematicStudyId, code, description)
    }

    fun buildTextual() = Textual(questionId, systematicStudyId, code, description)

    fun buildLabeledScale(scales: Map<String, Int>) = LabeledScale(questionId, systematicStudyId, code, description, scales)

    fun buildNumberScale(lower: Int, higher: Int) = NumberScale(questionId, systematicStudyId, code, description, lower, higher)

    fun buildPickList(options: List<String>) = PickList(questionId, systematicStudyId, code, description, options)

    fun buildPickMany(options: List<String>) = PickMany(questionId, systematicStudyId, code, description, options)
}


