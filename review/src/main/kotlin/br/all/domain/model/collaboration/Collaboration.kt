package br.all.domain.model.collaboration

import br.all.domain.model.review.SystematicStudyId
import br.all.domain.shared.ddd.Entity
import br.all.domain.shared.ddd.Notification
import br.all.domain.user.UserAccountId
import java.util.UUID

class Collaboration(
    id: CollaborationId,
    val systematicStudyId: SystematicStudyId,
    val userId: UserAccountId,
    status: CollaborationStatus = CollaborationStatus.ACTIVE,
    permissions: Set<CollaborationPermission> = emptySet()
    ): Entity<UUID>(id) {
        init{
            
        }
    
    private val _permissions = permissions.toMutableSet()
    val permissions get() = _permissions
    
    var status = status
        private set
    
    fun addPermission(permission: CollaborationPermission) = _permissions.add(permission)
    
    fun removePermission(permission: CollaborationPermission) = _permissions.remove(permission)

    fun removeCollaboration() {
        status = CollaborationStatus.REMOVED
    }
    
    private fun validate() = Notification().also{
        if(permissions.isEmpty())
            it.addError("Collaboration must have at least one permission")
    }
}