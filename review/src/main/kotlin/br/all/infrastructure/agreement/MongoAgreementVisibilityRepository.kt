package br.all.infrastructure.agreement

import org.springframework.data.mongodb.repository.MongoRepository
import java.util.UUID

interface MongoAgreementVisibilityRepository : MongoRepository<AgreementVisibilityDocument, UUID>