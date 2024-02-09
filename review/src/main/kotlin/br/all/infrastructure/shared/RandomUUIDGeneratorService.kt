package br.all.infrastructure.shared

import br.all.domain.services.UuidGeneratorService
import org.springframework.stereotype.Service
import java.util.*

@Service
class RandomUUIDGeneratorService : UuidGeneratorService {
    override fun next(): UUID = UUID.randomUUID()
}