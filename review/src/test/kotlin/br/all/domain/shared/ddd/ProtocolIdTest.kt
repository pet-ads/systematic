package br.all.domain.shared.ddd

import br.all.domain.model.protocol.ProtocolId
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*

internal class ProtocolIdTest{
    @Test
    fun `valid ProtocolId`() {
        val uuid = UUID.randomUUID()
        val protocolId = ProtocolId(uuid)
        assertEquals(uuid, protocolId.value())
    }

    @Test
    fun `equality of ProtocolIds`() {
        val uuid1 = UUID.randomUUID()
        val uuid2 = UUID.randomUUID()

        val protocolId1 = ProtocolId(uuid1)
        val protocolId2 = ProtocolId(uuid1)
        val protocolId3 = ProtocolId(uuid2)

        assertEquals(protocolId1, protocolId2) // ProtocolIds com o mesmo UUID devem ser iguais
        assertNotEquals(protocolId1, protocolId3) // ProtocolIds com UUIDs diferentes devem ser diferentes
    }

    @Test
    fun `validate ProtocolId - Invalid UUID`() {
        val invalidUuid = "invalid-uuid"
        assertThrows<IllegalArgumentException> {
            ProtocolId(UUID.fromString(invalidUuid))
        }
    }
}