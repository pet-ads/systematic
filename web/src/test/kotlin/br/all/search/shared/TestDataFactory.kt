package br.all.search.shared

import br.all.domain.shared.utils.paragraph
import br.all.infrastructure.search.SearchSessionDocument
import io.github.serpro69.kfaker.Faker
import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile
import java.time.LocalDateTime
import java.util.*

class TestDataFactory {
    val userId: UUID = UUID.randomUUID()
    val systematicStudyId: UUID = UUID.randomUUID()
    val sessionId: UUID = UUID.randomUUID()
    val nonExistentSessionId: UUID = UUID.randomUUID()
    private val faker = Faker()

    fun validPostRequest(user: UUID = userId, systematicStudyId: UUID = this.systematicStudyId) =
        """
        {
            "userId": "$user",
            "systematicStudyId": "$systematicStudyId",
            "source": "${faker.lorem.words()}",
            "searchString": "${faker.paragraph(5)}",
            "additionalInfo": "${faker.paragraph(30)}"
        }
        """.trimIndent()

    fun uniquenessViolationPostRequest(researcher: UUID = this.userId, systematicStudyId: UUID = this.systematicStudyId) =
        """
        {
            "researcherId": "$researcher",
            "systematicStudyId": "$systematicStudyId",
            "source": "Source",
            "searchString": "${faker.paragraph(5)}",
            "additionalInfo": "${faker.paragraph(30)}"
        }
        """.trimIndent()

    fun invalidPostRequest(
        userId: UUID = UUID.randomUUID(),
        systematicStudyId: UUID = UUID.randomUUID()
    ) = """
        {
            "userId": "$userId",
            "systematicStudyId": "$systematicStudyId",
            "source": "",
            "searchString": "",
            "additionalInfo": ""
        }
        """.trimIndent()

    fun bibfile() = MockMultipartFile(
        "file",
        "bibfile.bib",
        MediaType.TEXT_PLAIN_VALUE,
        """
            @ARTICLE{Gruneberg202458,
            author = {Gruneberg, Elena Solveig and Ramos-Guerrero, Jorge and Pastrana, Tania},
            title = {Challenges in the Provision of Pediatric Palliative Care in Mexico: A Cross-Sectional Web-Based Survey},
            year = {2024},
            journal = {Journal of Palliative Care},
            volume = {39},
            number = {1},
            pages = {58 – 67},
            doi = {10.1177/08258597211062767},
            url = {https://www.scopus.com/inward/record.uri?eid=2-s2.0-85121306924&doi=10.1177%2f08258597211062767&partnerID=40&md5=9688263b3b2c141a4b0f20624a8b93ab},
            abstract = {Objective: An enormous need for },
            author_keywords = {adolescent; barrier; challenge; children; health services accessibility; low- and middle-income countries; Mexico; pediatric palliative care},
            keywords = {Child; Cross-Sectional Studies; Female; Hospice and Palliative Care Nursing; Humans; Internet; Male; Mexico; Palliative Care; article; awareness; child; controlled study; convenience sample; female; financial management; health care access; health practitioner; human; human experiment; human tissue; male; medical profession; Mexico; middle income country; palliative therapy; paramedical education; work environment; work experience; cross-sectional study; Internet; Mexico; palliative nursing},
            type = {Article},
            publication_stage = {Final},
            source = {Scopus},
            note = {Cited by: 0; All Open Access, Green Open Access}
        }

        @ARTICLE{Staccini2022173,
            author = {Staccini, Pascal and Lau, Annie Y.S.},
            title = {Consuming Health Information and Vulnerable Populations: Factors of Engagement and Ongoing Usage},
            year = {2022},
            journal = {Yearbook of Medical Informatics},
            volume = {31},
            number = {1},
            pages = {173 – 180},
            doi = {10.1055/s-0042-1742549},
            abstract = {Objective: An enormous need for },
            author_keywords = {Consumer health informatics; health literacy; healthcare disparities; minority groups},
            keywords = {Adolescent; Aged; Child; COVID-19; Diabetes Mellitus, Type 2; Female; Health Behavior; Health Literacy; Health Status Disparities; Humans; Male; Young Adult; adolescent; aged; child; female; health behavior; health disparity; health literacy; human; male; non insulin dependent diabetes mellitus; young adult},
            type = {Article},
            publication_stage = {Final},
            source = {Scopus},
            note = {Cited by: 3; All Open Access, Green Open Access, Hybrid Gold Open Access}
            }
        """.toByteArray()
    )

    fun invalidBibFile() = "Invalid BibTeX file".toByteArray()

    fun searchSessionDocument(
        id: UUID = this.sessionId,
        systematicStudyId: UUID = this.systematicStudyId,
        user: UUID = this.userId,
        searchString: String = "SearchString",
        additionalInfo: String = "AdditionalInfo",
        timeStamp: LocalDateTime = LocalDateTime.of(2022, 1, 1, 0, 0),
        source: String = "Source"
    ): SearchSessionDocument {
        return SearchSessionDocument(
            id,
            systematicStudyId,
            user,
            searchString,
            additionalInfo,
            timeStamp,
            source,
        )
    }

    fun createValidPutRequest(
        searchString: String?,
        additionalInfo: String?,
        source: String?
    ): String {
        val jsonFields = mutableListOf<String>()

        if (searchString != null) {
            jsonFields.add("\"searchString\": \"$searchString\"")
        }

        if (additionalInfo != null) {
            jsonFields.add("\"additionalInfo\": \"$additionalInfo\"")
        }

        if (source != null) {
            jsonFields.add("\"source\": \"$source\"")
        }

        return "{ ${jsonFields.joinToString(", ")} }"
    }

    fun createUniquenessViolationPutRequest(
        source: String
    ): String {
        val jsonFields = mutableListOf<String>()
        jsonFields.add("\"source\": \"$source\"")
        return "{ ${jsonFields.joinToString(", ")} }"
    }

    fun validPatchRequest(
        user: UUID = userId,
        systematicStudyId: UUID = this.systematicStudyId,
        searchSessionId: UUID = this.sessionId
    ) = """
        {
            "userId": "$user",
            "systematicStudyId": "$systematicStudyId",
            "searchSessionId": "$searchSessionId"
        }
        """.trimIndent()
}