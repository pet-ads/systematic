package br.all.domain.model.question

import br.all.domain.model.review.SystematicStudyId
import br.all.domain.model.study.Answer
import br.all.domain.shared.ddd.Notification

class NumberScale(
    id: QuestionId,
    systematicStudyId: SystematicStudyId,
    code: String,
    description: String,
    val lower: Int,
    val higher: Int,
) : Question<Int>(id, systematicStudyId, code, description) {
    init {
        val notification = validate()
        require(notification.hasNoErrors()) { notification.message() }
    }

    fun validate(): Notification {
        val notification = Notification()
        if (higher < lower)
            notification.addError(
                "Higher value (${higher}) must be greater than \"lower\" (${lower}) value."
            )
        if (higher == lower)
            notification.addError(
                "Higher (${higher}) and lower (${lower}) values must be different."
            )
        return notification
    }

    override fun answer(value: Int): Answer<Int> {
        if (value > higher || value < lower)
            throw IllegalArgumentException("Answer is out of bounds [$lower, $higher]: $value")
        return Answer(id.value(), value)
    }

    override fun toString() = "NumberScale(questionId=$id, higher=$higher, lower=$lower)"
}