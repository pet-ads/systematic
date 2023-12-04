package br.all.domain.shared.valueobject

import br.all.domain.shared.ddd.Notification
import br.all.domain.shared.ddd.ValueObject

data class Text(val text: String) : ValueObject() {

    init {
        val notification = validate()
        require(notification.hasNoErrors()) { notification.message() }
    }

    override fun validate(): Notification {
        val notification = Notification()

        if (text.isEmpty()) notification.addError("Text must not be empty.")
        if (text.isBlank()) notification.addError("Text must not be blank.")

        val pattern = Regex("^(?![!@#$%¨&*()_+='<>,.:;|/?`´^~{}\\[\\]\"-]+).*")


        if(!pattern.matches(text)){
            notification.addError("Should be a valid text")
        }

        return notification
    }

}