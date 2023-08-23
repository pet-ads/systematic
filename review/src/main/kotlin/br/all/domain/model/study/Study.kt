package br.all.domain.model.study

import br.all.domain.shared.ddd.Notification
import br.all.domain.shared.ddd.ValueObject

data class Study(
    val title: String,
    val year: Int,
    val authors: String,
    val venue: String,
    val abstract: String,
    val keywords: Set<String>,
    val references: List<String>,
    val doi: Doi?
) : ValueObject() {

    init {
        val notification = validate()
        require(notification.hasNoErrors()) { notification.message() }
    }

    override fun validate(): Notification {
        val notification = Notification()
        if (title.isBlank()) notification.addError("Title field must not be blank.")
        if (authors.isBlank()) notification.addError("Authors field must not be blank.")
        if (year == 0) notification.addError("Publication year must not be zero.")
        if (venue.isBlank()) notification.addError("Journal field must not be blank.")
        if (abstract.isBlank()) notification.addError("Abstract field must not be blank.")
        return notification
    }
}