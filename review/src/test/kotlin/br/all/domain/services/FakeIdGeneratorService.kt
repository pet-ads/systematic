package br.all.domain.services

import java.util.UUID

object FakeIdGeneratorService : IdGeneratorService {
    private var currentId = 1L
    override fun next(reviewId: UUID) = currentId++
    override fun reset(reviewId: UUID) = run { currentId = 1L }
}