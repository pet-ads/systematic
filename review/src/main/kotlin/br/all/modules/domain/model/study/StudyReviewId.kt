package br.all.modules.domain.model.study

import br.all.modules.domain.common.ddd.Identifier
import br.all.modules.domain.common.ddd.Notification

data class StudyReviewId(val value: Long) : Identifier {

    init {
        val notification = validate()
        require(notification.hasNoErrors()) { notification.message() }
    }

    override fun validate(): Notification {
        val notification = Notification()
        if (value <= 0) notification.addError("Study review must be positive non-zero. Provided: $value")
        return notification
    }

    override fun toString() = value.toString()
}


