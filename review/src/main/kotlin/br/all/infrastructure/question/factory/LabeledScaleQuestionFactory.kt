package br.all.infrastructure.question.factory

import br.all.application.question.create.CreateQuestionService
import br.all.application.question.repository.QuestionDto
import br.all.application.question.shared.QuestionFactory
import br.all.domain.model.question.LabeledScale
import br.all.domain.model.question.Question
import br.all.domain.model.question.QuestionBuilder
import br.all.domain.model.question.QuestionId
import br.all.domain.model.review.SystematicStudyId
import br.all.infrastructure.question.MongoQuestionRepository
import br.all.infrastructure.question.QuestionRepositoryImpl
import java.util.*

//class LabeledScaleQuestionFactory(private val mongoRepository: MongoQuestionRepository): QuestionFactory {
//    override fun create(id: UUID, request: CreateQuestionService.RequestModel) = with(request){
//        if (scales == null) throw IllegalArgumentException("scales must not be null.")
//        QuestionBuilder.with(QuestionId(id), SystematicStudyId(systematicStudyId), code, description)
//            .buildLabeledScale(scales)
//    }
//
//    override fun toDto(question: Question<*>) = with(question as LabeledScale){
//        QuestionDto(id.value(), systematicStudyId.value(), code, description, "LabeledScale", scales = scales.mapValues { it.value.value })
//    }
//
//    override fun repository() = QuestionRepositoryImpl(mongoRepository)
//
//}