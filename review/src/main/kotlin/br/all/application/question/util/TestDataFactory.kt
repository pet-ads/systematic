package br.all.application.question.util

import br.all.application.question.create.CreateQuestionService.*
import br.all.application.question.repository.QuestionDto
import br.all.application.question.update.services.UpdateQuestionService
import br.all.domain.model.question.QuestionContextEnum
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
        code: String = faker.lorem.words(),
        description: String = faker.lorem.words(),
    ) = QuestionDto(
        questionId,
        systematicStudyId,
        code,
        description,
        "TEXTUAL",
        null,
        null,
        null,
        null,
        context = QuestionContextEnum.EXTRACTION
    )

    fun generatePickListDto(
        questionId: UUID = question,
        systematicStudyId: UUID = systematicStudy,
    ) = QuestionDto(
        questionId,
        systematicStudyId,
        code,
        description,
        "PICK_LIST",
        null,
        null,
        null,
        listOf(faker.lorem.words(), faker.lorem.words()),
        context = QuestionContextEnum.EXTRACTION
    )

    fun generateLabeledScaleDto(
        questionId: UUID = question,
        systematicStudyId: UUID = systematicStudy,

    ) = QuestionDto(
        questionId,
        systematicStudyId,
        code,
        description,
        "LABELED_SCALE",
        mapOf(faker.lorem.words() to 1, faker.lorem.words() to 2),
        null,
        null,
        null,
        context = QuestionContextEnum.EXTRACTION
    )

    fun generateNumberedScaleDto(
        questionId: UUID = question,
        systematicStudyId: UUID = systematicStudy,
    ) = QuestionDto(
        questionId,
        systematicStudyId,
        code,
        description,
        "NUMBERED_SCALE",
        null,
        10,
        1,
        null,
        context = QuestionContextEnum.EXTRACTION
    )

    fun updateQuestionRequestModel(
        updatedDto: QuestionDto,
        questionType: QuestionType,
        researcherId: UUID = researcher,
        context: String = QuestionContextEnum.EXTRACTION.toString(),
    ) = UpdateQuestionService.RequestModel(
        researcherId,
        updatedDto.systematicStudyId,
        updatedDto.questionId,
        questionType,
        context,
        updatedDto.code,
        updatedDto.description,
        updatedDto.scales,
        updatedDto.higher,
        updatedDto.lower,
        updatedDto.options
    )

    fun createTextualRequestModel(
        researcherId: UUID = researcher,
        systematicStudyId: UUID = systematicStudy,
        questionType: QuestionType = QuestionType.TEXTUAL,
        context: String = QuestionContextEnum.EXTRACTION.toString(),
    ) = RequestModel(
        researcherId,
        systematicStudyId,
        context,
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
        options: List<String>? = listOf(faker.lorem.words(), faker.lorem.words()),
        context: String = QuestionContextEnum.EXTRACTION.toString(),
    ) = RequestModel(
        researcherId,
        systematicStudyId,
        context,
        questionType,
        code,
        description,
        null,
        null,
        null,
        options
    )

    fun createLabeledScaleRequestModel(
        researcherId: UUID = researcher,
        systematicStudyId: UUID = systematicStudy,
        questionType: QuestionType = QuestionType.LABELED_SCALE,
        scales: Map<String, Int>? = mapOf(faker.lorem.words() to 1, faker.lorem.words() to 2),
        context: String = QuestionContextEnum.EXTRACTION.toString(),
    ) = RequestModel(
        researcherId,
        systematicStudyId,
        context,
        questionType,
        code,
        description,
        scales,
        null,
        null,
        null
    )

    fun createNumberedScaleRequestModel(
        researcherId: UUID = researcher,
        systematicStudyId: UUID = systematicStudy,
        questionType: QuestionType = QuestionType.NUMBERED_SCALE,
        higher: Int? = 10,
        lower: Int? = 1,
        context: String = QuestionContextEnum.EXTRACTION.toString(),
    ) = RequestModel(
        researcherId,
        systematicStudyId,
        context,
        questionType,
        code,
        description,
        null,
        higher,
        lower,
        null
    )

    fun updateQuestionResponseModel(
        researcherId: UUID = researcher,
        systematicStudyId: UUID = systematicStudy,
        questionId: UUID = question,
    ) = UpdateQuestionService.ResponseModel(
        researcherId, systematicStudyId, questionId
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
        systematicStudyId: UUID = systematicStudy
    ) = QuestionDto(
        questionId,
        systematicStudyId,
        request.code,
        request.description,
        request.questionType.toString(),
        request.scales,
        request.higher,
        request.lower,
        request.options,
        context = QuestionContextEnum.EXTRACTION
    )

    operator fun component1() = researcher
    operator fun component2() = systematicStudy
    operator fun component3() = question
}
