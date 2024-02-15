package br.all.application.protocol.repository

import java.util.*

interface ProtocolRepository {
    fun saveOrUpdate(dto: ProtocolDto)

    fun findById(id: UUID): ProtocolDto?

    fun findBySystematicStudy(systematicStudyId: UUID): ProtocolDto?

    fun findAll(): List<ProtocolDto>

    fun existsById(id: UUID): Boolean

    fun existsBySystematicStudy(systematicStudyId: UUID): Boolean
}