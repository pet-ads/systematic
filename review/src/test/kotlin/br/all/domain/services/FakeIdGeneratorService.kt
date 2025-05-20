package br.all.domain.services

import br.all.domain.services.IdGeneratorService

object FakeIdGeneratorService : IdGeneratorService {
    private var currentId = 1L
    override fun next() = currentId++
    override fun reset() = run { currentId = 1L }
}