package br.all.domain.model.review

import br.all.domain.common.ddd.Identifier
import br.all.domain.common.ddd.Notification
import java.util.UUID

@JvmInline
value class ReviewId(val value: UUID) : Identifier {
    init {
        val notification = validate()
        require(notification.hasNoErrors()) { notification.message() }
    }

    override fun validate(): Notification = Notification()

    override fun toString(): String {
        return value.toString()
    }
}
