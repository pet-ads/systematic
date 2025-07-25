package br.all.domain.model.question

import br.all.domain.shared.ddd.Identifier
import br.all.domain.shared.ddd.Notification
import java.util.UUID

@JvmInline
value class QuestionId(val value : UUID) : Identifier<UUID> {
    override fun validate() = Notification()
    override fun value(): UUID = value
    override fun toString() = value.toString()

}
