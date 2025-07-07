package br.all.domain.model.search

import br.all.domain.model.protocol.SearchSource
import br.all.domain.model.user.ResearcherId
import br.all.domain.model.review.SystematicStudyId
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDateTime
import java.util.*

import kotlin.test.assertEquals

@Tag("UnitTest")
class SearchSessionTest {


    @Test
    fun `testando getId`() {
        val searchSessionId = SearchSessionID(UUID.randomUUID())
        val systematicStudyId = SystematicStudyId(UUID.randomUUID())
        val researcherId = ResearcherId(UUID.randomUUID())
        val searchString = "Example Search String"
        val additionalInfo = "Additional Info"
        val timestamp = LocalDateTime.now()
        val source = SearchSource("Example Source")

        val searchSession = SearchSession(
            searchSessionId,
            systematicStudyId,
            researcherId,
            searchString,
            additionalInfo,
            timestamp,
            source
        )

        assertEquals(searchSessionId, searchSession.id)
    }

    @Test
    fun `testando getSearchString`() {
        val searchSessionId = SearchSessionID(UUID.randomUUID())
        val systematicStudyId = SystematicStudyId(UUID.randomUUID())
        val researcherId = ResearcherId(UUID.randomUUID())
        val searchString = "Example Search String"
        val additionalInfo = "Additional Info"
        val timestamp = LocalDateTime.now()
        val source = SearchSource("Example Source")

        val searchSession = SearchSession(
            searchSessionId,
            systematicStudyId,
            researcherId,
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
        val systematicStudyId = SystematicStudyId(UUID.randomUUID())
        val researcherId = ResearcherId(UUID.randomUUID())
        val searchString = "Example Search String"
        val additionalInfo = "Additional Info"
        val timestamp = LocalDateTime.now()
        val source = SearchSource("Example Source")

        val searchSession = SearchSession(
            searchSessionId,
            systematicStudyId,
            researcherId,
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
        val systematicStudyId = SystematicStudyId(UUID.randomUUID())
        val researcherId = ResearcherId(UUID.randomUUID())
        val searchString = "Example Search String"
        val additionalInfo = "Additional Info"
        val timestamp = LocalDateTime.now()
        val source = SearchSource("Example Source")

        val searchSession = SearchSession(
            searchSessionId,
            systematicStudyId,
            researcherId,
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
        val systematicStudyId = SystematicStudyId(UUID.randomUUID())
        val researcherId = ResearcherId(UUID.randomUUID())
        val searchString = "Example Search String"
        val additionalInfo = "Additional Info"
        val timestamp = LocalDateTime.now()
        val source = SearchSource("Example Source")

        val searchSession = SearchSession(
            searchSessionId,
            systematicStudyId,
            researcherId,
            searchString,
            additionalInfo,
            timestamp,
            source
        )

        assertEquals(source, searchSession.source)
    }

    @Test
    fun `Should create a valid SearchSession`() {
        val searchSessionId = SearchSessionID(UUID.randomUUID())
        val systematicStudyId = SystematicStudyId(UUID.randomUUID())
        val researcherId = ResearcherId(UUID.randomUUID())
        val searchString = "Search string"
        val additionalInfo = "Additional info"
        val timestamp = LocalDateTime.now()
        val source = SearchSource("Search source")

        val searchSession = SearchSession(
            searchSessionId,
            systematicStudyId,
            researcherId,
            searchString,
            additionalInfo,
            timestamp,
            source
        )

        assertEquals(searchSessionId, searchSession.id)
        assertEquals(systematicStudyId, searchSession.systematicStudyId)
        assertEquals(searchString, searchSession.searchString)
        assertEquals(additionalInfo, searchSession.additionalInfo)
        assertEquals(timestamp, searchSession.timestamp)
        assertEquals(source, searchSession.source)
    }

    @Test
    fun `Should fail due blank string on searchString`() {

        assertThrows<IllegalArgumentException> {
            val searchSessionId = SearchSessionID(UUID.randomUUID())
            val systematicStudyId = SystematicStudyId(UUID.randomUUID())
            val researcherId = ResearcherId(UUID.randomUUID())
            val searchString = ""
            val additionalInfo = "Additional info"
            val timestamp = LocalDateTime.now()
            val source = SearchSource("Search Source")

            val searchSession = SearchSession(
                searchSessionId,
                systematicStudyId,
                researcherId,
                searchString,
                additionalInfo,
                timestamp,
                source
            )
        }
    }
}
