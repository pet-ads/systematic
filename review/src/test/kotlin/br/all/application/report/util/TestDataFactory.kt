package br.all.application.report.util

import br.all.application.protocol.repository.CriterionDto
import br.all.application.question.repository.QuestionDto
import br.all.application.study.repository.AnswerDto
import br.all.domain.model.question.QuestionContextEnum
import io.github.serpro69.kfaker.Faker
import java.util.UUID

class TestDataFactory {
    val faker = Faker()
    val questionId: UUID = UUID.randomUUID()

    fun criteriaDto(
        description: String = faker.leagueOfLegends.quote(),
        type: String = faker.clashOfClans.defensiveBuildings(),
    ): CriterionDto {
        return CriterionDto(
            description = description,
            type = type,
        )
    }

    fun questionDto(
        questionId: UUID = UUID.randomUUID(),
        systematicStudyId: UUID = UUID.randomUUID(),
        code: String = faker.lorem.words(),
        description: String = faker.lorem.words(),
        questionType: String = "TEXTUAL",
        questionContext: QuestionContextEnum = QuestionContextEnum.EXTRACTION,
    ): QuestionDto {
        return QuestionDto(
            questionId = questionId,
            systematicStudyId = systematicStudyId,
            code = code,
            description = description,
            questionType = questionType,
            scales = null,
            higher = null,
            lower = null,
            options = null,
            context = questionContext
        )
    }

    fun answerDto(
        studyReviewId: Long = faker.random.nextLong(),
        answer: String = faker.lorem.words()
    ): AnswerDto {
        return AnswerDto(
            studyReviewId = studyReviewId,
            answer = answer
        )
    }
}