package br.all.domain.model.session

import br.all.domain.shared.ddd.Identifier
import br.all.domain.shared.ddd.Notification

class SearchSessionId(val value : String) : Identifier {
    override fun validate(): Notification {
        TODO("Not yet implemented")
    }
}