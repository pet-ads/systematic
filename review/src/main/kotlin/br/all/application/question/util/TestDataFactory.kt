package br.all.application.question.util

import br.all.application.question.create.CreateQuestionService.*
import br.all.application.question.repository.QuestionDto
import io.github.serpro69.kfaker.Faker
import java.util.*
import br.all.application.question.find.FindQuestionService as Find

class TestDataFactory {
    private val faker = Faker()
    val systematicStudy: UUID = UUID.randomUUID()
    val researcher: UUID = UUID.randomUUID()
    val question: UUID = UUID.randomUUID()
    val code: String = faker.lorem.words()
    val description: String = faker.lorem.words()

    fun generateTextualDto(
        questionId: UUID = question,
        systematicStudyId: UUID = systematicStudy,
    ) =
        QuestionDto(
            questionId,
            systematicStudyId,
            faker.lorem.words(),
            faker.lorem.words(),
            "TEXTUAL",
            null,
            null,
            null,
            null
    )

    fun generatePickListDto(
        questionId: UUID = question,
        systematicStudyId: UUID = systematicStudy,
    ) =
        QuestionDto(
            questionId,
            systematicStudyId,
            code,
            description,
            "PICK_LIST",
            null,
            null,
            null,
            listOf(faker.lorem.words(), faker.lorem.words())
        )

    fun generateLabeledScaleDto(
        questionId: UUID = question,
        systematicStudyId: UUID = systematicStudy,
    ) =
        QuestionDto(
            questionId,
            systematicStudyId,
            code,
            description,
            "LABELED_SCALE",
            mapOf(faker.lorem.words() to 1, faker.lorem.words() to 2),
            null,
            null,
            null
        )

    fun generateNumberedScaleDto(
        questionId: UUID = question,
        systematicStudyId: UUID = systematicStudy,
    ) =
        QuestionDto(
            questionId,
            systematicStudyId,
            code,
            description,
            "NUMBERED_SCALE",
            null,
            1,
            10,
            null,
        )

    fun createTextualRequestModel(
        researcherId: UUID = researcher,
        systematicStudyId: UUID = systematicStudy,
        questionType: QuestionType = QuestionType.TEXTUAL,
    ) = RequestModel(
        researcherId,
        systematicStudyId,
        questionType,
        code,
        description,
        null,
        null,
        null,
        null
    )

    fun createPickListRequestModel(
        researcherId: UUID = researcher,
        systematicStudyId: UUID = systematicStudy,
        questionType: QuestionType = QuestionType.PICK_LIST,
    ) = RequestModel(
        researcherId,
        systematicStudyId,
        questionType,
        code,
        description,
        null,
        null,
        null,
        listOf(faker.lorem.words(), faker.lorem.words())
    )

    fun createLabeledScaleRequestModel(
        researcherId: UUID = researcher,
        systematicStudyId: UUID = systematicStudy,
        questionType: QuestionType = QuestionType.LABELED_SCALE,
    ) = RequestModel(
        researcherId,
        systematicStudyId,
        questionType,
        code,
        description,
        mapOf(faker.lorem.words() to 1, faker.lorem.words() to 2),
        null,
        null,
        null
    )

    fun createNumberedScaleRequestModel(
        researcherId: UUID = researcher,
        systematicStudyId: UUID = systematicStudy,
        questionType: QuestionType = QuestionType.NUMBERED_SCALE,
    ) = RequestModel(
        researcherId,
        systematicStudyId,
        questionType,
        code,
        description,
        null,
        10,
        1,
        null
    )

    fun findOneQuestionRequestModel(
        researcherId: UUID = this.researcher,
        systematicStudyId: UUID = this.systematicStudy,
        questionId: UUID = this.question
    ) = Find.RequestModel(researcherId, systematicStudyId, questionId)

    fun findOneTextualResponseModel(
        researcherId: UUID = this.researcher,
        questionDto: QuestionDto = generateTextualDto()
    ) = Find.ResponseModel(researcherId, questionDto)

    fun findOnePickListResponseModel(
        researcherId: UUID = this.researcher,
        questionDto: QuestionDto = generatePickListDto()
    ) = Find.ResponseModel(researcherId, questionDto)

    fun findOneLabeledScaleResponseModel(
        researcherId: UUID = this.researcher,
        questionDto: QuestionDto = generateLabeledScaleDto()
    ) = Find.ResponseModel(researcherId, questionDto)

    fun findOneNumberedScaleResponseModel(
        researcherId: UUID = this.researcher,
        questionDto: QuestionDto = generateNumberedScaleDto()
    ) = Find.ResponseModel(researcherId, questionDto)

    fun dtoFromRequest(
        request: RequestModel,
        questionId: UUID = question,
        systematicStudyId: UUID = systematicStudy,
    ) = QuestionDto(
        questionId,
        systematicStudyId,
        request.code,
        request.description,
        request.questionType.toString(),
        request.scales,
        request.lower,
        request.higher,
        request.options
    )


    operator fun component1() = researcher
    operator fun component2() = systematicStudy
    operator fun component3() = question
}