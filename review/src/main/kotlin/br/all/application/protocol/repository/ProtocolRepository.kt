package br.all.application.protocol.repository

import java.util.*

interface ProtocolRepository {
    fun create(dto: ProtocolDto)

    fun findById(id: UUID): ProtocolDto?

    fun findAll(): List<ProtocolDto>

    fun existsById(id: UUID): Boolean
}