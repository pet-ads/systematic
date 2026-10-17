package br.all.infrastructure.agreement

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.UUID

@Document("agreement_visibility")
data class AgreementVisibilityDocument(
    @Id val id: UUID,
    val selection: String,
    val extraction: String,
)