package br.all.protocol.persistence

import br.all.infrastructure.protocol.MongoProtocolRepository
import br.all.infrastructure.shared.toNullable
import br.all.protocol.shared.TestDataFactory
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
@Tag("IntegrationTest")
class MongoProtocolRepositoryTest(
    @Autowired private val sut: MongoProtocolRepository,
) {
    private lateinit var factory: TestDataFactory

    @BeforeEach
    fun setUp() {
        factory = TestDataFactory()
        sut.deleteAll()
    }

    @Nested
    @Tag("ValidClasses")
    @DisplayName("When being able to save protocols")
    inner class WhenBeingAbleToSaveProtocols {
        @Test
        fun `should save the protocol`() {
            val document = factory.createProtocolDocument()
            sut.save(document)
            assertEquals(document, sut.findById(factory.protocol).toNullable())
        }
    }
}
