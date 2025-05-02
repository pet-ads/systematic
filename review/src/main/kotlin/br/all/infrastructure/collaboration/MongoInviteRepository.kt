package br.all.infrastructure.collaboration

import org.springframework.data.mongodb.repository.MongoRepository
import java.util.*

interface MongoInviteRepository: MongoRepository<InviteDocument, UUID>