package br.all.domain.model.protocol.question

import br.all.domain.model.protocol.ProtocolId
import org.junit.jupiter.api.Test
import java.util.UUID
import kotlin.test.assertIs

class QuestionTest{

    @Test
    fun `should create TextualQuestion using builder`(){
        val id = QuestionId(UUID.randomUUID())
        val protocolId = ProtocolId(UUID.randomUUID())
        val textual = QuestionBuilder2()
            .with(id, protocolId, "Q1", "Palmeiras tem mundial?")
            .buildTextual()
        //TODO add assertions for check if properties are correctly set
        assertIs<Textual>(textual)
    }

    @Test
    fun `should create NumberScale using builder`(){
        val id = QuestionId(UUID.randomUUID())
        val protocolId = ProtocolId(UUID.randomUUID())
        val scale = QuestionBuilder2()
            .with(id, protocolId, "Q2", "Quantos mundiais tem o Palmeiras")
            .buildNumberScale(0, 20)
        //TODO add assertions for check if properties are correctly set
        assertIs<NumberScale>(scale)
    }


}