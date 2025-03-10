package br.all.domain.model.protocol

import br.all.domain.shared.ddd.Notification
import br.all.domain.shared.ddd.ValueObject

data class Picoc(
    val population: String? = null,
    val intervention: String? = null,
    val control: String? = null,
    val outcome: String? = null,
    val context: String? = null,
) : ValueObject() {
    init {
        val notification = validate()
        require(notification.hasNoErrors()) { notification.message() }
    }

    override fun validate() = Notification()

    companion object
}