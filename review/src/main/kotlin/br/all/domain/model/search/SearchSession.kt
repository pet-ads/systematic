package br.all.domain.model.search

import br.all.domain.model.protocol.SearchSource
import br.all.domain.model.review.SystematicStudyId
import br.all.domain.shared.ddd.Entity
import br.all.domain.shared.ddd.Notification
import java.time.LocalDateTime
import java.util.*


class SearchSession(
    searchSessionId: SearchSessionID,
    val systematicStudyId: SystematicStudyId,
    var searchString: String,
    var additionalInfo: String?,
    val timestamp: LocalDateTime = LocalDateTime.now(),
    var source: SearchSource
) : Entity<UUID>(searchSessionId){
    init {
        val notification = validate()
        require(notification.hasNoErrors()) { notification.message() }
    }

    fun validate() = Notification().also {
        if (searchString.isBlank()) it.addError("The search string must not be blank!")
    }

    companion object
}