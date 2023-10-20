package br.all.domain.model.protocol.question
import br.all.domain.model.protocol.ProtocolId

class Textual(
    id: QuestionId,
    protocolId: ProtocolId,
    code: String,
    description: String,
): Question<String>(id, protocolId, code, description) {

    override fun validateAnswer(value: String?): String {
        if (value == null) throw IllegalArgumentException("Answer must not be null.")
        if (value.isBlank()) throw IllegalArgumentException("Answer must not be blank.")
        return value
    }
}