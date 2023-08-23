package br.all.infrastructure.shared

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document("database_sequences")
data class DatabaseSequence (@Id val id: String, val sequence: Long)
