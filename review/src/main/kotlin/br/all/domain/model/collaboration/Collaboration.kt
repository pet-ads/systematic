package br.all.domain.model.collaboration

import br.all.domain.model.review.SystematicStudyId
import br.all.domain.model.user.ResearcherId
import br.all.domain.shared.ddd.Entity
import br.all.domain.shared.ddd.Notification
import java.util.*

class Collaboration(
    id: CollaborationId,
    val systematicStudyId: SystematicStudyId,
    val userId: ResearcherId,
    status: CollaborationStatus = CollaborationStatus.ACTIVE,
    permissions: Set<CollaborationPermission> = emptySet()
) : Entity<UUID>(id) {

    private val _permissions = permissions.toMutableSet()
    val permissions: Set<CollaborationPermission>
        get() = _permissions.toSet() 

    var status = status
        private set

    init {
        val notification = validate()
        require(notification.hasNoErrors()) { notification.message() }
    }
    
    fun addPermission(permission: CollaborationPermission) {
        _permissions.add(permission)
    }

    fun removePermission(permission: CollaborationPermission) {
        _permissions.remove(permission)
    }

    fun removeCollaboration() {
        status = CollaborationStatus.REMOVED
    }

    private fun validate() = Notification().also {
        if (_permissions.isEmpty())
            it.addError("Collaboration must have at least one permission")
    }
}