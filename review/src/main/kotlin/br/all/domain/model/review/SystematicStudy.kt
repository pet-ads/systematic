package br.all.domain.model.review

import br.all.domain.model.protocol.ProtocolId
import br.all.domain.model.researcher.ResearcherId
import br.all.domain.model.session.SearchSessionId
import br.all.domain.shared.ddd.Entity

// todo: replace type title and description properties from String to Text when it is implemented
class SystematicStudy(
    id : ReviewId,
    val protocolId : ProtocolId,
    val title : String,
    val description : String,
    val researchers : MutableSet<ResearcherId> = mutableSetOf(),
    val searchSessions : MutableSet<SearchSessionId> = mutableSetOf(),
) : Entity(id) {
    // todo: backing properties for researcher and searchSessions?
    init {
        require(researchers.isNotEmpty()) { "There must be at least one researcher working on this systematic study!" }
    }

    fun addResearcher(researcherId : ResearcherId) = researchers.add(researcherId)

    fun removeResearcher(researcherId: ResearcherId) {
        if (researchers.size == 1)
            throw IllegalStateException("Unable to remove the last researcher! There must be at least one researcher" +
                    " working on this systematic study!")
        researchers.remove(researcherId)
    }

    fun addSearchSession(sessionId: SearchSessionId) = searchSessions.add(sessionId)

    override fun toString() : String {
        return "SystematicStudy(protocolId=$protocolId, title='$title', description='$description', " +
                "researchers=$researchers, searchSessions=$searchSessions)"
    }
}