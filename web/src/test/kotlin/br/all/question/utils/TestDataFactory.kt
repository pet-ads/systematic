package br.all.question.utils

import br.all.domain.model.question.QuestionContextEnum
import br.all.domain.shared.utils.paragraph
import br.all.infrastructure.question.QuestionDocument
import io.github.serpro69.kfaker.Faker
import java.util.*

class TestDataFactory {
    val researcherId: UUID = UUID.randomUUID()
    val systematicStudyId: UUID = UUID.randomUUID()
    val questionId: UUID = UUID.randomUUID()
    private val faker = Faker()

    val fakerWord: String
        get() = faker.lorem.words()

    fun validCreateTextualRequest(researcher: UUID = researcherId, systematicStudyId: UUID = this.systematicStudyId) =
        """
        {
            "researcherId": "$researcher",
            "systematicStudyId": "$systematicStudyId",
            "type": "TEXTUAL",
            "code": "${faker.lorem.words()}",
            "description": "${faker.paragraph(8)}"
        }
        """

    fun validCreatePickListRequest(researcher: UUID = researcherId, systematicStudyId: UUID = this.systematicStudyId) =
        """
        {
            "researcherId": "$researcher",
            "systematicStudyId": "$systematicStudyId",
            "type": "PICK_LIST",
            "code": "${faker.lorem.words()}",
            "description": "${faker.paragraph(8)}",
            "options": [
                "${faker.lorem.words()}",
                "${faker.lorem.words()}",
                "${faker.lorem.words()}"
            ]
        }
        """

    fun validCreateLabeledScaleRequest(researcher: UUID = researcherId, systematicStudyId: UUID = this.systematicStudyId) =
        """
        {
            "researcherId": "$researcher",
            "systematicStudyId": "$systematicStudyId",
            "type": "LABELED_SCALE",
            "code": "${faker.lorem.words()}",
            "description": "${faker.paragraph(8)}",
            "scales": {
                "${faker.lorem.words()}": 1,
                "${faker.lorem.words()}": 2,
                "${faker.lorem.words()}": 3
            }
        }
        """

    fun validCreateNumberScaleRequest(researcher: UUID = researcherId, systematicStudyId: UUID = this.systematicStudyId) =
        """
        {
            "researcherId": "$researcher",
            "systematicStudyId": "$systematicStudyId",
            "type": "NUMBERED_SCALE",
            "code": "${faker.lorem.words()}",
            "description": "${faker.paragraph(8)}",
            "higher": "${10}",
            "lower": "${1}"
        }
        """

    fun invalidCreateTextualRequest(researcher: UUID = researcherId, systematicStudyId: UUID = this.systematicStudyId) =
        """
        {
            "researcherId": "$researcher",
            "systematicStudyId": "$systematicStudyId",
            "type": "TEXTUAL",
            "code": "${faker.lorem.words()}",
            "description": ""
        }
        """

    fun invalidCreatePickListRequest(researcher: UUID = researcherId, systematicStudyId: UUID = this.systematicStudyId) =
        """
        {
            "researcherId": "$researcher",
            "systematicStudyId": "$systematicStudyId",
            "type": "PICK_LIST",
            "code": "${faker.lorem.words()}",
            "description": "${faker.paragraph(8)}",
            "options": []
        }
        """

    fun invalidCreateLabeledScaleRequest(researcher: UUID = researcherId, systematicStudyId: UUID = this.systematicStudyId) =
        """
        {
            "researcherId": "$researcher",
            "systematicStudyId": "$systematicStudyId",
            "type": "LABELED_SCALE",
            "code": "${faker.lorem.words()}",
            "description": "${faker.paragraph(8)}",
            "scales": {}
        }
        """

    fun invalidCreateNumberScaleRequest(researcher: UUID = researcherId, systematicStudyId: UUID = this.systematicStudyId) =
        """
        {
            "researcherId": "$researcher",
            "systematicStudyId": "$systematicStudyId",
            "type": "NUMBERED_SCALE",
            "code": "${faker.lorem.words()}",
            "description": "${faker.paragraph(8)}",
            "higher": "${1}",
            "lower": "${10}"
        }
        """

    fun validCreateTextualQuestionDocument(
        questionId: UUID,
        systematicStudyId: UUID,
        code: String = faker.lorem.words(),
        description: String = faker.lorem.words(),
        questionType: QuestionContextEnum = QuestionContextEnum.ROB

    ) = QuestionDocument(
            questionId,
            systematicStudyId,
            code,
            description,
            "TEXTUAL",
            null,
            null,
            null,
            null, 
            questionType
        )

    fun validCreatePickListQuestionDocument(
        questionId: UUID,
        systematicStudyId: UUID,
        code: String = faker.lorem.words(),
        description: String = faker.lorem.words(),
        questionType: QuestionContextEnum = QuestionContextEnum.ROB
        ) = QuestionDocument(
        questionId,
        systematicStudyId,
        code,
        description,
        "PICK_LIST",
        null,
        null,
        null,
        listOf(faker.lorem.words(), faker.lorem.words()),
        questionType
    )

    fun validCreateLabeledScaleQuestionDocument(
        questionId: UUID,
        systematicStudyId: UUID,
        code: String = faker.lorem.words(),
        description: String = faker.lorem.words(),
        questionType: QuestionContextEnum = QuestionContextEnum.ROB
        ) = QuestionDocument(
        questionId,
        systematicStudyId,
        code,
        description,
        "LABELED_SCALE",
        mapOf(faker.lorem.words() to 1, faker.lorem.words() to 2),
        null,
        null,
        null,
        questionType
    )

    fun validCreateNumberedScaleQuestionDocument(
        questionId: UUID,
        systematicStudyId: UUID,
        code: String = faker.lorem.words(),
        description: String = faker.lorem.words(),
        questionType: QuestionContextEnum = QuestionContextEnum.ROB
        ) = QuestionDocument(
        questionId,
        systematicStudyId,
        code,
        description,
        "NUMBERED_SCALE",
        null,
        10,
        1,
        null,
        questionType
    )
}