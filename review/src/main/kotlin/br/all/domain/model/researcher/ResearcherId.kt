package br.all.domain.model.researcher

import br.all.domain.shared.ddd.Identifier
import br.all.domain.shared.ddd.Notification
import java.util.*

@JvmInline
value class ResearcherId(val value : UUID ) : Identifier <UUID> {

    override fun validate() = Notification()

    override fun value(): UUID = value

    override fun toString() = value.toString()
}

fun UUID.toResearcherId() = ResearcherId(UUID.randomUUID())
