package br.all.domain.services

interface IdGeneratorService {
    fun next() : Long
}