//package br.all.application.protocol.question.update
//
//import br.all.application.protocol.question.create.labeledScale.LabeledScaleRequestModel
//import br.all.application.protocol.question.create.labeledScale.LabeledScaledDTO
//import br.all.application.protocol.question.create.numberScale.NumberScaleDTO
//import br.all.application.protocol.question.create.numberScale.NumberScaleRequestModel
//import br.all.application.protocol.question.create.pickList.PickListDTO
//import br.all.application.protocol.question.create.pickList.PickListRequestModel
//import br.all.application.question.create.textual.TextualDTO
//import br.all.application.protocol.question.create.textual.TextualRequestModel
//import br.all.application.protocol.question.repository.*
//import br.all.domain.model.protocol.question.QuestionBuilder
//import br.all.domain.model.protocol.question.QuestionId
//import br.all.domain.services.UuidGeneratorService
//import java.util.UUID
//
//class UpdateQuestionService(
//    private val uuidGenerator: UuidGeneratorService,
//    private val labeledScaleRepository: LabeledScaleRepository,
//    private val pickListRepository: PickListRepository,
//    private val numberScaleRepository: NumberScaleRepository,
//    private val textualRepository: TextualRepository
//) {
//    fun updateLabeledScale(questionId: QuestionId, data: LabeledScaleRequestModel): LabeledScaledDTO {
//        labeledScaleRepository.update(labeledScaledDTO)
//        return labeledScaleRepository.findById(questionId)
//    }
//
//    fun createPickList(data: PickListRequestModel): PickListDTO{
//        val questionId = QuestionId(uuidGenerator.next())
//        val pickList = QuestionBuilder.newBuilder(questionId, data.protocolId, data.code, data.description)
//            .pickListType(data.options)
//            .buildPickList()
//        pickListRepository.create(pickList.toDto())
//
//        return pickListRepository.findById(questionId)
//    }
//
//    fun createNumberScale(data: NumberScaleRequestModel) : NumberScaleDTO{
//        val questionId = QuestionId(uuidGenerator.next())
//        val numberScale = QuestionBuilder.newBuilder(questionId, data.protocolId, data.code, data.description)
//            .numberScaleType(data.higher)
//            .numberScaleLower(data.lower)
//            .buildNumberScale()
//        numberScaleRepository.create(numberScale.toDto())
//
//        return numberScaleRepository.findById(questionId)
//    }
//
//    fun createTextual(data: TextualRequestModel) : TextualDTO{
//        val questionId = QuestionId(uuidGenerator.next())
//        val textual = QuestionBuilder.newBuilder(questionId, data.protocolId, data.code, data.description)
//            .buildTextualQuestion()
//        textualRepository.create(textual.toDto())
//
//        return textualRepository.findById(questionId)
//    }
//}