package br.all.infrastructure.report

import br.all.domain.services.JsonParserService
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue

class JacksonJsonParserService : JsonParserService {

    private val mapper = jacksonObjectMapper()

    override fun parseJsonObject(json: String): Map<String, String> {
        return mapper.readValue(json)
    }
}
