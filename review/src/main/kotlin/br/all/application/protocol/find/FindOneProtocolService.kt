package br.all.application.protocol.find

import br.all.application.protocol.repository.ProtocolDto
import java.util.*

interface FindOneProtocolService {
    fun findById(id: UUID): ProtocolDto?

    fun findBySystematicStudy(reviewId: UUID): ProtocolDto?

    fun existsById(id: UUID): Boolean

    fun existsBySystematicStudy(reviewId: UUID): Boolean
}