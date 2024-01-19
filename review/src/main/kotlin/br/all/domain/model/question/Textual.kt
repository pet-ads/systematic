package br.all.domain.model.question

import br.all.domain.model.protocol.ProtocolId
import br.all.domain.model.study.Answer

class Textual(
    id: QuestionId,
    protocolId: ProtocolId,
    code: String,
    description: String
) : Question<String>(id, protocolId, code, description) {

    override fun answer(value: String): Answer<String> {
        if (value.isBlank()) throw IllegalArgumentException("Answer must not be blank.")
        return Answer(id.value(), value)
    }
}