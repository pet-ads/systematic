package br.all.infrastructure.collaboration

import br.all.application.collaboration.repository.CollaborationDto
import br.all.application.collaboration.repository.CollaborationRepository
import br.all.application.collaboration.repository.InviteDto
import java.util.*

class CollaborationRepositoryImpl(
    private val innerCollaborationRepository: MongoCollaborationRepository,
    private val innerInviteRepository: MongoInviteRepository
) 
    : CollaborationRepository 
{
    override fun saveOrUpdateCollaboration(dto: CollaborationDto) {
        innerCollaborationRepository.save(CollaborationDocument(
            dto.id,
            dto.systematicStudyId,
            dto.userId,
            dto.status,
            dto.permissions
        ))
    }

    override fun saveOrUpdateInvite(dto: InviteDto) {
        innerInviteRepository.save(InviteDocument(
            dto.id,
            dto.systematicStudyId,
            dto.userId,
            dto.inviteDate,
            dto.expirationDate
        ))
    }

    override fun listAllCollaborationsBySystematicStudyId(id: UUID): List<CollaborationDto> {
        return innerCollaborationRepository.findAll().filter { it.systematicStudyId == id }.map { 
            CollaborationDto(it.id, it.systematicStudyId, it.userId, it.status, it.permissions)
        }
    }

    override fun listAllInvitesBySystematicStudyId(id: UUID): List<InviteDto> {
        return innerInviteRepository.findAll().filter { it.systematicStudyId == id }.map { 
            InviteDto(it.id, it.systematicStudyId, it.userId, it.inviteDate, it.expirationDate)
        }
    }
}