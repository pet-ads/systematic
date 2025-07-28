package br.all.domain.model.review

import br.all.domain.model.collaboration.Collaboration
import br.all.domain.model.collaboration.CollaborationId
import br.all.domain.model.collaboration.toCollaborationId
import br.all.domain.model.user.ResearcherId
import br.all.domain.model.user.toResearcherId
import br.all.domain.shared.ddd.Entity
import br.all.domain.shared.ddd.Notification
import br.all.domain.shared.utils.exists
import java.util.*

class SystematicStudy(
    id: SystematicStudyId,
    title: String,
    description: String,
    owner: ResearcherId,
    collaborators: Set<CollaborationId>
) : Entity<UUID>(id) {

    private val _collaborators = collaborators.toMutableSet()
    val collaborators get() = _collaborators.toSet()
    var owner = owner
        private set
    var title: String = title
        set(value) {
            require(value.isNotBlank()) { "Systematic study title must not be blank." }
            field = value
        }
    var description = description
        set(value) {
            require(value.isNotBlank()) { "Systematic study description must not be blank." }
            field = value
        }

    init {
        val notification = validate()
        require(notification.hasNoErrors()) { notification.message() }
    }

    private fun validate() = Notification().also {
        if (title.isBlank())
            it.addError("Systematic Study title must not be blank!")
        if (description.isBlank())
            it.addError("Systematic Study description must not be blank!")
    }

    fun addCollaborator(collaborationId: CollaborationId) = _collaborators.add(collaborationId)

    fun changeOwner(previousOwnerCollaborationId: CollaborationId, newCollaboration: Collaboration) {
        _collaborators.remove(previousOwnerCollaborationId)
        owner = newCollaboration.userId
        _collaborators.add(newCollaboration.id as CollaborationId)
    }   

    fun removeCollaborator(collaboration: Collaboration) {
        check(collaboration.id.value().toResearcherId() != owner) { "Cannot remove the Systematic Study owner: $owner" }
        exists(collaboration.id.value().toCollaborationId() in _collaborators) {
            "Cannot remove member that is not part of the collaboration: ${collaboration.id}"
        }
        _collaborators.remove(collaboration.id)
    }

    override fun toString() = "SystematicStudy(reviewId=$id, title='$title', description='$description', owner=$owner," +
            " researchers=$_collaborators)"

    companion object
}
