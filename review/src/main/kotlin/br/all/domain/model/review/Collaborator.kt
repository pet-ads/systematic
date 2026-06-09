package br.all.domain.model.review

import br.all.domain.shared.ddd.Entity
import br.all.domain.shared.user.Email
import br.all.domain.shared.user.ResearcherId
import br.all.domain.shared.user.Role
import java.util.*

class Collaborator(
    id: ResearcherId,
    val systematicStudyId: SystematicStudyId,
    val username: String,
    val email: Email,
    val role: Role,
) : Entity<UUID>(id){
    companion object
}
