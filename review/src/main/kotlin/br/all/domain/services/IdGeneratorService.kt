package br.all.domain.services

import java.util.UUID

interface IdGeneratorService {
    fun next(reviewId: UUID): Long
    fun reset(reviewId: UUID) : Any
}
