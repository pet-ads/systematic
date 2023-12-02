package br.all.domain.model.protocol

import br.all.domain.shared.ddd.Notification
import br.all.domain.shared.ddd.ValueObject

data class Picoc(
    val population: String,
    val intervention: String,
    val control: String,
    val outcome: String,
    val context: String?,
) : ValueObject() {
    override fun validate() = Notification()
}