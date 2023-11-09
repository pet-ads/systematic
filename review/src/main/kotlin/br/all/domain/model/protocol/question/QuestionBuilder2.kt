package br.all.domain.model.protocol.question

import br.all.domain.model.protocol.ProtocolId

class QuestionBuilder2 {

    private var questionId: QuestionId? = null
    private var protocolId: ProtocolId? = null
    private var code: String? = null
    private var description: String? = null

    fun with(id: QuestionId, protocolId: ProtocolId, code: String, description: String) =
        QuestionBuilder2().apply {
            this.questionId = id
            this.protocolId = protocolId
            this.code = code
            this.description = description
        }

    fun buildTextual() =
        Textual(questionId!!, protocolId!!, code!!, description!!)

    fun buildLabeledScale(scales: Map<String, Int>) =
        LabeledScale(questionId!!, protocolId!!, code!!, description!!, scales)

    fun buildNumberScale(lower: Int, higher: Int) =
        NumberScale(questionId!!, protocolId!!, code!!, description!!, lower, higher)

    fun buildPickList(options: List<String>) =
        PickList(questionId!!, protocolId!!, code!!, description!!, options)

}


