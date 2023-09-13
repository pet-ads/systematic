package br.all.domain.services

import java.util.*

interface UuidGeneratorService {
    fun next() : UUID
}