package br.all.domain.model.protocol.question
import br.all.domain.model.protocol.ProtocolId

class Textual(
    questionId: QuestionId,
    protocolId: ProtocolId,
    code: String,
    description: String,
):
    Question<String>(questionId, protocolId, code, description) {
    override fun validateAnswer(value: String?): String {
        if (value.isNullOrBlank())
            throw IllegalArgumentException("Answer can not be null or blank.")
        return value
    }
}