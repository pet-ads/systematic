package br.all.domain.model.protocol

import br.all.domain.shared.ddd.Notification
import br.all.domain.shared.ddd.ValueObject

data class Picoc(
    val population: String,
    val intervention: String,
    val control: String,
    val outcome: String,
    val context: String? = null,
) : ValueObject() {
    init {
        val notification = validate()
        require(notification.hasNoErrors()) { notification.message() }
    }

    override fun validate() = Notification().also {
        if (population.isBlank())
            it.addError("The population described in the PICOC must not be blank!")
        if (intervention.isBlank())
            it.addError("The intervention described in the PICOC must not be blank!")
        if (control.isBlank())
            it.addError("The control described in the PICOC must not be blank!")
        if (outcome.isBlank())
            it.addError("The outcome described in the PICOC must not be blank!")
        if (context != null && context.isBlank())
            it.addError("The context, when provided, must not be blank!")
    }
}