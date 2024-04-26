package br.all.domain.user

import br.all.domain.shared.ddd.Identifier
import br.all.domain.shared.ddd.Notification
import java.util.UUID

@JvmInline
value class UserAccountId(val value: UUID = UUID.randomUUID()) : Identifier<UUID> {
    override fun validate() = Notification()
    override fun value(): UUID = value
    override fun toString() = value.toString()
}
