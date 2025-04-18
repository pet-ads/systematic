package br.all.infrastructure

import br.all.application.protocol.repository.ProtocolDto
import br.all.application.review.util.TestDataFactory
import br.all.infrastructure.protocol.MongoProtocolRepository
import br.all.infrastructure.protocol.ProtocolDocument
import br.all.infrastructure.protocol.ProtocolRepositoryImpl
import br.all.infrastructure.protocol.toDocument
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.*

@Tag("UnitTests")
@Tag("ServiceTests")
@ExtendWith(MockKExtension::class)
class ProtocolRepositoryImplTest {
    @MockK
    private lateinit var innerRepository: MongoProtocolRepository

    private lateinit var factory: TestDataFactory

    @InjectMockKs
    private lateinit var protocolRepositoryImpl: ProtocolRepositoryImpl

    @BeforeEach
    fun setUp() {
        factory = TestDataFactory()
    }

    private fun createProtocolPair(): Pair<ProtocolDto, ProtocolDocument> {
        val dto = factory.protocolDto()
        return dto to dto.toDocument()
    }

    @Test
    fun `should save or update in repository`() {
        val (protocolDto, protocolDocument) = createProtocolPair()

        every { innerRepository.save(protocolDocument) } returns protocolDocument

        protocolRepositoryImpl.saveOrUpdate(protocolDto)

        verify(exactly = 1) { innerRepository.save(protocolDocument) }
    }

    @Test
    fun `should return dto when found in repository`() {
        val (protocolDto, protocolDocument) = createProtocolPair()

        every { innerRepository.findById(protocolDocument.id) } returns Optional.of(protocolDocument)

        val result = protocolRepositoryImpl.findById(protocolDto.id)

        assertThat(result).isNotNull()
        assertThat(result).isEqualTo(protocolDto)
        verify(exactly = 1) { innerRepository.findById(protocolDto.id) }
    }

    @Test
    fun `should return null when not found in repository`() {
        val (protocolDto, _) = createProtocolPair()

        every { innerRepository.findById(protocolDto.id) } returns Optional.empty()

        val result = protocolRepositoryImpl.findById(protocolDto.id)

        assertThat(result).isNull()
        verify(exactly = 1) { innerRepository.findById(protocolDto.id) }
    }

    @Test
    fun `should verify if exists in repository`() {
        val protocolDto = factory.protocolDto()
        val protocolDocument = protocolDto.toDocument()

        every { innerRepository.existsById(protocolDocument.id) } returns true

        val result = protocolRepositoryImpl.existsById(protocolDto.id)

        assertThat(result).isTrue()
        assertThat(protocolDto.id).isEqualTo(protocolDto.id)
        verify(exactly = 1) { innerRepository.existsById(protocolDto.id) }
    }
}