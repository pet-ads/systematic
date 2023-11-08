package br.all.application.protocol.question.create

import br.all.application.protocol.question.create.labeledScale.LabeledScaleRequestModel
import br.all.application.protocol.question.create.labeledScale.LabeledScaledDTO
import br.all.application.protocol.question.repository.QuestionRepository
import br.all.application.protocol.question.repository.toDto
import br.all.domain.model.protocol.question.QuestionBuilder
import br.all.domain.model.protocol.question.QuestionId
import br.all.domain.services.UuidGeneratorService

class CreateQuestionService(
    private val uuidGenerator: UuidGeneratorService,
    private val repository: QuestionRepository
) {
    fun createLabeledScale(data: LabeledScaleRequestModel): LabeledScaledDTO {
        val questionId = QuestionId(uuidGenerator.next())
        val labeledScale = QuestionBuilder.newBuilder(questionId, data.protocolId, data.code, data.description)
            .labeledScaleType(data.scales)
            .buildLabeledScale()
        repository.create(labeledScale)
        return labeledScale.toDto()
    }
}