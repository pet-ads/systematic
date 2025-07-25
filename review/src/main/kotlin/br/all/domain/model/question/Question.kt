package br.all.domain.model.question

import br.all.domain.shared.ddd.Entity
import br.all.domain.model.review.SystematicStudyId
import br.all.domain.model.study.Answer
import br.all.domain.shared.ddd.Notification
import java.util.*

abstract class Question<T>(
    questionId: QuestionId,
    val systematicStudyId: SystematicStudyId,
    val code: String,
    val description: String,
) : Entity<UUID>(questionId) {
    init {
        val notification = validate()
        require(notification.hasNoErrors()) { notification.message() }
    }

    private fun validate(): Notification {
        val notification = Notification()

        if (code.isBlank())
            notification.addError("Question code must not be blank. Provided: \"$code\"")
        if (description.isBlank())
            notification.addError("Question description must not be blank. Provided: \"$description\"")

        return notification
    }

    abstract fun answer(value: T): Answer<T>
    companion object
}