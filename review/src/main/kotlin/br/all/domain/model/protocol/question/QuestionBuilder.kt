package br.all.domain.model.protocol.question

import br.all.domain.model.protocol.ProtocolId

class QuestionBuilder private constructor() {
    companion object {
        fun newBuilder(): FirstIdStep = Steps()
        //questioStepBUilder.newBuilder.step1().step2()
        interface FirstIdStep {
            fun questionCalled(id: QuestionId): ProtocolIdStep
        }

        interface ProtocolIdStep {
            fun protocolIdStep(protocolId: ProtocolId): CodeStep
        }

        interface CodeStep {
            fun codeStep(code: String): DescriptionStep
        }

        interface DescriptionStep {
            fun descriptionStep(description: String): TypeStep
        }

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
