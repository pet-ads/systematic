package br.all.domain.services

interface JsonParserService {
    fun parseJsonObject(json: String): Map<String, String>
}
