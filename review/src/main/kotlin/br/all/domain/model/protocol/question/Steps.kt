package br.all.domain.model.protocol.question

import br.all.domain.model.protocol.question.QuestionBuilder.Companion.BuildLabeledScaleStep
import br.all.domain.model.protocol.question.QuestionBuilder.Companion.BuildNumberScaleStep
import br.all.domain.model.protocol.question.QuestionBuilder.Companion.BuildPickListStep
import br.all.domain.model.protocol.question.QuestionBuilder.Companion.NumberScaleLowerStep
import br.all.domain.model.protocol.question.QuestionBuilder.Companion.TypeStep
import br.all.domain.model.protocol.ProtocolId

class Steps(
    private var id: QuestionId,
    private var protocolId: ProtocolId,
    private var code: String,
    private var description: String,
    private var scales: Map<String, Int> = emptyMap(),
    private var higher: Int = 0,
    private var lower: Int = 0,
    private var options: List<String> = emptyList()


) : TypeStep, BuildLabeledScaleStep, BuildPickListStep, BuildNumberScaleStep, NumberScaleLowerStep{

    override fun labeledScaleType(scales: Map<String, Int>): BuildLabeledScaleStep {
        this.scales = scales
        return this
    }

    override fun buildLabeledScale(): LabeledScale {
        return LabeledScale(id, protocolId, code, description, scales)
    }

    override fun numberScaleType(higher: Int): NumberScaleLowerStep {
        this.higher = higher
        return this
    }

    override fun numberScaleLower(lower: Int): BuildNumberScaleStep {
        this.lower = lower
        return this
    }

    override fun buildNumberScale(): NumberScale {
        return NumberScale(id, protocolId, code, description, higher, lower)
    }

    override fun pickListType(options: List<String>): BuildPickListStep {
        this.options = options
        return this
    }

    override fun buildPickList(): PickList {
        return PickList(id, protocolId, code, description, options)
    }

    override fun buildTextualQuestion(): Textual {
        return Textual(id, protocolId, code, description)
    }
}
