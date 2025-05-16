package br.all.application.collaboration.repository

import java.util.*

interface CollaborationRepository {
    fun saveOrUpdateCollaboration(dto: CollaborationDto)
    fun saveOrUpdateInvite(dto: InviteDto)
    
    fun listAllCollaborationsBySystematicStudyId(id: UUID): List<CollaborationDto>
    fun listAllInvitesBySystematicStudyId(id: UUID): List<InviteDto>
    
    fun deleteInvite(id: UUID)
}