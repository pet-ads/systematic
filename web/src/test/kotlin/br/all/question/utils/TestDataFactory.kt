package br.all.question.utils

import br.all.domain.shared.utils.paragraph
import io.github.serpro69.kfaker.Faker
import java.util.*

class TestDataFactory {
    val researcherId: UUID = UUID.randomUUID()
    val systematicStudyId: UUID = UUID.randomUUID()
    private val faker = Faker()

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
}