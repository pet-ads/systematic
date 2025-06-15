package br.all.domain.model.review

import br.all.domain.model.collaboration.CollaborationId
import br.all.domain.shared.ddd.Entity
import br.all.domain.shared.ddd.Notification
import br.all.domain.shared.utils.exists
import java.util.*

class SystematicStudy(
    id: SystematicStudyId,
    title: String,
    description: String,
    owner: CollaborationId,
    collaborators: Set<CollaborationId> = emptySet(),
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
        _collaborators.add(owner)
    }

    private fun validate() = Notification().also {
        if (title.isBlank())
            it.addError("Systematic Study title must not be blank!")
        if (description.isBlank())
            it.addError("Systematic Study description must not be blank!")
    }

    fun addCollaborator(collaborationId: CollaborationId) = _collaborators.add(collaborationId)

    fun changeOwner(collaborationId: CollaborationId) {
        _collaborators.add(collaborationId)
        owner = collaborationId
    }

    fun removeCollaborator(collaborationId: CollaborationId) {
        check(collaborationId != owner) { "Cannot remove the Systematic Study owner: $owner" }
        exists(collaborationId in _collaborators) {
            "Cannot remove member that is not part of the collaboration: $collaborationId"
        }
        _collaborators.remove(collaborationId)
    }

    override fun toString() = "SystematicStudy(reviewId=$id, title='$title', description='$description', owner=$owner," +
            " researchers=$_collaborators)"

    companion object
}
