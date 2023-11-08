//package br.all.application.protocol.question.create
//
//import br.all.application.protocol.question.create.labeledScale.LabeledScaleRequestModel
//import br.all.application.protocol.question.create.labeledScale.LabeledScaledDTO
//import br.all.application.protocol.question.repository.QuestionRepository
//import br.all.application.protocol.question.repository.toDto
//import br.all.domain.model.protocol.ProtocolId
//import br.all.domain.model.protocol.question.QuestionId
//import br.all.domain.services.UuidGeneratorService
//
//class CreateQuestionService(
//    private val uuidGenerator: UuidGeneratorService,
//    private val repository: QuestionRepository
//) {
//    fun createLabeledScale(labeledScaleRequestModel: LabeledScaleRequestModel): LabeledScaledDTO {
//        val questionId = QuestionId(uuidGenerator.next())
//        val labeledScale = QuestionStepBuilder.newBuilder()
//            .questionCalled(questionId)
//            .protocolIdStep(labeledScaleRequestModel.protocolId)
//            .codeStep(labeledScaleRequestModel.code)
//            .descriptionStep(labeledScaleRequestModel.description)
//            .labeledScaleType(labeledScaleRequestModel.scales)
//            .buildLabeledScale()
//        repository.create(labeledScale)
//        return labeledScale.toDto()
//    }
//}