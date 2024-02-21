package br.all.application.protocol.repository

import java.util.*

interface ProtocolRepository {
    fun saveOrUpdate(dto: ProtocolDto)

    fun findById(id: UUID): ProtocolDto?

    fun existsById(id: UUID): Boolean
}