package br.all.domain.shared.user

import br.all.domain.shared.ddd.Entity
import java.util.UUID

class Researcher (
    id: ResearcherId,
    val username: String,
    val roles: Set<Role>
) : Entity<UUID>(id)