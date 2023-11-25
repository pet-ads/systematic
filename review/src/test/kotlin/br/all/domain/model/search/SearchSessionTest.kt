package br.all.domain.model.search

import br.all.domain.model.protocol.ProtocolId
import br.all.domain.model.protocol.SearchSource
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.util.*

import kotlin.test.assertEquals

class SearchSessionTest {

    @Test
    fun `testando toString`() {
        val searchSessionId = SearchSessionID(UUID.randomUUID())
        val protocolId = ProtocolId(UUID.randomUUID())
        val searchString = "Example Search String"
        val additionalInfo = "Additional Info"
        val timestamp = LocalDateTime.now()
        val source = SearchSource("Example Source")

        val searchSession = SearchSession(
            searchSessionId,
            protocolId,
            searchString,
            additionalInfo,
            timestamp,
            source
        )

        val expectedToString = "SearchSession(searchSessionId=$searchSessionId, protocolId=$protocolId, searchString=$searchString, additionalInfo=$additionalInfo, timestamp=$timestamp, source=$source)"
        assertEquals(expectedToString, searchSession.toString())
    }

    @Test
    fun `testando getId`() {
        val searchSessionId = SearchSessionID(UUID.randomUUID())
        val protocolId = ProtocolId(UUID.randomUUID())
        val searchString = "Example Search String"
        val additionalInfo = "Additional Info"
        val timestamp = LocalDateTime.now()
        val source = SearchSource("Example Source")

        val searchSession = SearchSession(
            searchSessionId,
            protocolId,
            searchString,
            additionalInfo,
            timestamp,
            source
        )

        assertEquals(searchSessionId, searchSession.searchSessionId)
    }

    @Test
    fun `testando getSearchString`() {
        val searchSessionId = SearchSessionID(UUID.randomUUID())
        val protocolId = ProtocolId(UUID.randomUUID())
        val searchString = "Example Search String"
        val additionalInfo = "Additional Info"
        val timestamp = LocalDateTime.now()
        val source = SearchSource("Example Source")

        val searchSession = SearchSession(
            searchSessionId,
            protocolId,
            searchString,
            additionalInfo,
            timestamp,
            source
        )

        assertEquals(searchString, searchSession.searchString)
    }

    @Test
    fun `testando getAdditionalInfo`() {
        val searchSessionId = SearchSessionID(UUID.randomUUID())
        val protocolId = ProtocolId(UUID.randomUUID())
        val searchString = "Example Search String"
        val additionalInfo = "Additional Info"
        val timestamp = LocalDateTime.now()
        val source = SearchSource("Example Source")

        val searchSession = SearchSession(
            searchSessionId,
            protocolId,
            searchString,
            additionalInfo,
            timestamp,
            source
        )

        assertEquals(additionalInfo, searchSession.additionalInfo)
    }

    @Test
    fun `testando getTimestamp`() {
        val searchSessionId = SearchSessionID(UUID.randomUUID())
        val protocolId = ProtocolId(UUID.randomUUID())
        val searchString = "Example Search String"
        val additionalInfo = "Additional Info"
        val timestamp = LocalDateTime.now()
        val source = SearchSource("Example Source")

        val searchSession = SearchSession(
            searchSessionId,
            protocolId,
            searchString,
            additionalInfo,
            timestamp,
            source
        )

        assertEquals(timestamp, searchSession.timestamp)
    }

    @Test
    fun `testando getSource`() {
        val searchSessionId = SearchSessionID(UUID.randomUUID())
        val protocolId = ProtocolId(UUID.randomUUID())
        val searchString = "Example Search String"
        val additionalInfo = "Additional Info"
        val timestamp = LocalDateTime.now()
        val source = SearchSource("Example Source")

        val searchSession = SearchSession(
            searchSessionId,
            protocolId,
            searchString,
            additionalInfo,
            timestamp,
            source
        )

        assertEquals(source, searchSession.source)
    }
}
