package br.all.domain.model.protocol

import br.all.application.shared.CouldNotCreateEntityException
import br.all.application.study.repository.StudyReviewDto
import br.all.domain.shared.ddd.Notification
import br.all.domain.shared.ddd.ValueObject

data class SearchSource(val searchSource: String) : ValueObject() {

    init {
        val notification = validate()
        require(notification.hasNoErrors()) { notification.message() }
    }

    override fun validate(): Notification {
        val notification = Notification()

        if (searchSource.isEmpty()) notification.addError("SearchSource must not be empty.")
        if (searchSource.isBlank()) notification.addError("SearchSource must not be blank.")

        return notification
    }
}
