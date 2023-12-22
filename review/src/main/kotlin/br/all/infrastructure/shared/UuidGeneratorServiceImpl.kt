package br.all.infrastructure.shared

import br.all.domain.services.UuidGeneratorService
import java.util.*

class UuidGeneratorServiceImpl: UuidGeneratorService {
    override fun next(): UUID = UUID.randomUUID()
}