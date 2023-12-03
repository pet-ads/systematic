package br.all.application.protocol.question.create

import br.all.application.question.create.labeledScale.LabeledScaleRequestModel
import br.all.application.question.create.labeledScale.LabeledScaledDTO
import br.all.application.protocol.question.create.numberScale.NumberScaleDTO
import br.all.application.question.create.numberScale.NumberScaleRequestModel
import br.all.application.question.create.pickList.PickListDTO
import br.all.application.question.create.pickList.PickListRequestModel
import br.all.application.protocol.question.create.textual.TextualDTO
import br.all.application.question.create.textual.TextualRequestModel
import br.all.application.question.repository.*
import br.all.domain.model.question.QuestionBuilder
import br.all.domain.model.question.QuestionId
import br.all.domain.services.UuidGeneratorService

class CreateQuestionService(
    private val uuidGenerator: UuidGeneratorService,
    private val labeledScaleRepository: LabeledScaleRepository,
    private val pickListRepository: PickListRepository,
    private val numberScaleRepository: NumberScaleRepository,
    private val textualRepository: TextualRepository
) {
    fun createLabeledScale(data: LabeledScaleRequestModel): LabeledScaledDTO {
        val questionId = QuestionId(uuidGenerator.next())
        val labeledScale = QuestionBuilder()
            .with(questionId, data.protocolId, data.code, data.description)
            .buildLabeledScale(data.scales)

        labeledScaleRepository.create(labeledScale.toDto())

        return labeledScaleRepository.findById(questionId)
    }

    fun createPickList(data: PickListRequestModel): PickListDTO{
        val questionId = QuestionId(uuidGenerator.next())
        val pickList = QuestionBuilder()
            .with(questionId, data.protocolId, data.code, data.description)
            .buildPickList(data.options)

        pickListRepository.create(pickList.toDto())

        return pickListRepository.findById(questionId)
    }

    fun createNumberScale(data: NumberScaleRequestModel) : NumberScaleDTO{
        val questionId = QuestionId(uuidGenerator.next())
        val numberScale = QuestionBuilder()
            .with(questionId, data.protocolId, data.code, data.description)
            .buildNumberScale(data.lower, data.higher)

        numberScaleRepository.create(numberScale.toDto())

        return numberScaleRepository.findById(questionId)
    }

    fun createTextual(data: TextualRequestModel) : TextualDTO{
        val questionId = QuestionId(uuidGenerator.next())
        val textual = QuestionBuilder()
            .with(questionId, data.protocolId, data.code, data.description)
            .buildTextual()
        textualRepository.create(textual.toDto())

        return textualRepository.findById(questionId)
    }
}