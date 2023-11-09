package br.all.domain.model.protocol.question

import br.all.domain.model.protocol.ProtocolId

class QuestionBuilder private constructor() {
    companion object {

        fun newBuilder(
            id: QuestionId,
            protocolId: ProtocolId,
            code: String,
            description: String
        ): TypeStep = Steps(id, protocolId, code, description)


        interface TypeStep {
            fun labeledScaleType(scales: Map<String, Int>): BuildLabeledScaleStep
            fun numberScaleType(higher: Int): NumberScaleLowerStep
            fun pickListType(options: List<String>): BuildPickListStep
            fun buildTextualQuestion(): Textual
        }

        interface BuildLabeledScaleStep {
            fun buildLabeledScale(): LabeledScale
        }

        interface NumberScaleLowerStep {
            fun numberScaleLower(lower: Int): BuildNumberScaleStep
        }

        interface BuildNumberScaleStep {
            fun buildNumberScale(): NumberScale
        }

        interface BuildPickListStep {
            fun buildPickList(): PickList
        }
    }

}
