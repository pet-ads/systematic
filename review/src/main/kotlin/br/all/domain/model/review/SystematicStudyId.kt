package br.all.domain.model.review

import br.all.domain.shared.ddd.Identifier
import br.all.domain.shared.ddd.Notification
import java.util.UUID

@JvmInline
value class SystematicStudyId(val value: UUID) : Identifier {

    init {
        val notification = validate()
        require(notification.hasNoErrors()) { notification.message() }
    }

    override fun validate(): Notification = Notification()

    override fun toString() = value.toString()
}


