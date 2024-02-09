package br.all.question.utils

import br.all.domain.model.study.StudyType
import br.all.domain.shared.utils.jsonWordsArray
import br.all.domain.shared.utils.paragraph
import br.all.domain.shared.utils.year
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
}