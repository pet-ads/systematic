package br.all.domain.model.researcher

import br.all.domain.shared.ddd.Identifier
import br.all.domain.shared.ddd.Notification
import java.util.UUID

class ResearcherId(val value : UUID ) : Identifier {
    override fun validate() = Notification()

    override fun toString() = value.toString()
}