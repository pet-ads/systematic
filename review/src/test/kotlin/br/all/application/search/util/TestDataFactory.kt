package br.all.application.search.util

import br.all.application.protocol.repository.CriterionDto
import br.all.application.protocol.repository.PicocDto
import br.all.application.protocol.repository.ProtocolDto
import br.all.application.search.find.service.FindAllSearchSessionsBySourceService
import br.all.application.search.find.service.FindAllSearchSessionsService
import br.all.application.search.find.service.FindSearchSessionService
import br.all.application.search.repository.SearchSessionDto
import br.all.application.search.update.UpdateSearchSessionService
import br.all.application.search.update.UpdateSearchSessionService.RequestModel
import br.all.domain.model.protocol.SearchSource
import br.all.domain.model.user.ResearcherId
import br.all.domain.model.review.SystematicStudyId
import br.all.domain.model.search.SearchSession
import br.all.domain.model.search.SearchSessionID
import br.all.domain.shared.utils.paragraph
import io.github.serpro69.kfaker.Faker
import java.time.LocalDateTime
import java.util.*
import br.all.application.search.create.CreateSearchSessionService.RequestModel as CreateRequestModel
import br.all.application.search.create.CreateSearchSessionService.ResponseModel as CreateResponseModel

class TestDataFactory {
    val userId: UUID = UUID.randomUUID()
    val systematicStudyId: UUID = UUID.randomUUID()
    val searchSessionId: UUID = UUID.randomUUID()
    val source: String = "TestSearchSource"

    private val faker = Faker()

    fun createRequestModel(
        userId: UUID = this.userId,
        systematicStudyId: UUID = this.systematicStudyId,
        source: String = faker.paragraph(5),
        searchString: String = faker.paragraph(5),
        additionalInfo: String? = faker.paragraph(5),
    ) = CreateRequestModel(
        userId,
        systematicStudyId,
        source,
        searchString,
        additionalInfo,
    )

    fun searchSessionFromCreateRequest(
        request: CreateRequestModel,
        sessionId: UUID = this.searchSessionId,
        timestamp: LocalDateTime = LocalDateTime.now(),
    ) = SearchSession(
        SearchSessionID(sessionId),
        SystematicStudyId(request.systematicStudyId),
        ResearcherId(request.userId),
        request.searchString,
        request.additionalInfo ?: "",
        timestamp,
        SearchSource(request.source)
    )

    fun createResponseModel(
        userId: UUID = this.userId,
        systematicStudyId: UUID = this.systematicStudyId,
        sessionId: UUID = this.searchSessionId,
        invalidEntries: List<String> = emptyList()
    ) = CreateResponseModel(userId, systematicStudyId, sessionId, invalidEntries)

    fun findOneRequestModel(
        userId: UUID = this.userId,
        systematicStudyId: UUID = this.systematicStudyId,
        sessionId: UUID = this.searchSessionId
    ) = FindSearchSessionService.RequestModel(
        userId,
        systematicStudyId,
        sessionId
    )

    fun findAllRequestModel(
        userId: UUID = this.userId,
        systematicStudyId: UUID = this.systematicStudyId,
    ) = FindAllSearchSessionsService.RequestModel(userId, systematicStudyId)

    fun findAllBySourceRequestModel(
        userId: UUID = this.userId,
        systematicStudyId: UUID = this.systematicStudyId,
        source: String = this.source
    ) = FindAllSearchSessionsBySourceService.RequestModel(userId, systematicStudyId, source)

    fun generateDto(
        id: UUID = searchSessionId,
        systematicStudyId: UUID = this.systematicStudyId,
        userId: UUID = this.userId,
        searchString: String = "SearchString",
        additionalInfo: String = "",
        timeStamp: LocalDateTime = LocalDateTime.now(),
        source: String = "SearchSource",
        numberOfRelatedStudies: Int = 0
    ) = SearchSessionDto(
        id,
        systematicStudyId,
        userId,
        searchString,
        additionalInfo,
        timeStamp,
        source,
        numberOfRelatedStudies
    )

    fun generateProtocol(
        id: UUID = UUID.randomUUID(),
        systematicStudy: UUID = this.systematicStudyId,
        goal: String = "Goal",
        justification: String = "Justification",
        researchQuestion: Set<String> = setOf("question"),
        keyWords: Set<String> = setOf("keyWords"),
        searchString: String = "searchString",
        informationSources: Set<String> = setOf(this.source),
        sourcesSelectionCriteria: String = "criteria",
        searchMethod: String = "method",
        studiesLanguage: Set<String> = setOf("English"),
        studyTypeDefinition: String = "Definition",
        selectionProcess: String = "Process",
        eligibilityCriteria: Set<CriterionDto> = setOf(CriterionDto("Description", "Type")),
        dataCollectionProcess: String = "Collection",
        analysisAndSynthesisProcess: String = "SynthesisProcess",
        extractionQuestions: Set<UUID> = setOf(UUID.randomUUID()),
        robQuestions: Set<UUID> = setOf(UUID.randomUUID()),
        picoc: PicocDto = PicocDto("10000","Intevention", "Control", "Outcome", "")
    ) = ProtocolDto(
        id,
        systematicStudy,
        goal,
        justification,
        researchQuestion,
        keyWords,
        searchString,
        informationSources,
        sourcesSelectionCriteria,
        searchMethod,
        studiesLanguage,
        studyTypeDefinition,
        selectionProcess,
        eligibilityCriteria,
        dataCollectionProcess,
        analysisAndSynthesisProcess,
        extractionQuestions,
        robQuestions,
        picoc
    )

    fun findOneResponseModel(
        userId: UUID = this.userId,
    ) = FindSearchSessionService.ResponseModel(
        userId,
        (generateDto(id = this.searchSessionId, timeStamp = LocalDateTime.now()))
    )

    fun findAllResponseModel(
        amountOfSearchSessions: Int,
        userId: UUID = this.userId,
    ) = FindAllSearchSessionsService.ResponseModel(
        userId,
        systematicStudyId,
        List(amountOfSearchSessions) {
            generateDto(
                id = UUID.randomUUID(),
                timeStamp = LocalDateTime.now()
            )
        }
    )

    fun findAllBySourceResponseModel(
        amountOfSearchSessions: Int,
        userId: UUID = this.userId,
        source: String = this.source
    ) = FindAllSearchSessionsBySourceService.ResponseModel(
        userId,
        systematicStudyId,
        source,
        List(amountOfSearchSessions) {
            generateDto(
                id = UUID.randomUUID(),
                timeStamp = LocalDateTime.now(),
                source = this.source
            )
        }
    )

    fun emptyFindAllResponseModel(
        userId: UUID = this.userId,
    ) = FindAllSearchSessionsService.ResponseModel(
        userId,
        systematicStudyId,
        searchSessions = emptyList(),
    )

    fun emptyFindBySourceResponseModel(
        userId: UUID = this.userId,
    ) = FindAllSearchSessionsBySourceService.ResponseModel(
        userId,
        systematicStudyId,
        source,
        searchSessions = emptyList()
    )

    fun updateRequestModel(
        userId: UUID = this.userId,
        systematicStudy: UUID = systematicStudyId,
        session: UUID = searchSessionId,
        searchString: String = "SearchString",
        additionalInfo: String = "",
        source: String = "SearchSource"
    ) = RequestModel(userId, systematicStudy, session, searchString, additionalInfo, source)

    fun updateResponseModel(
        userId: UUID = this.userId,
        systematicStudyId: UUID = this.systematicStudyId,
        sessionId: UUID = this.searchSessionId,
    ) = UpdateSearchSessionService.ResponseModel(userId, systematicStudyId, sessionId)

    fun bibFileContent() =
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
        """

    operator fun component1() = userId

    operator fun component2() = systematicStudyId

    operator fun component3() = searchSessionId

    fun createResponseModelWithInvalidEntries(
        userId: UUID = this.userId,
        systematicStudyId: UUID = this.systematicStudyId,
        sessionId: UUID = this.searchSessionId,
        invalidEntries: List<String> = listOf("Invalid entry 1", "Invalid entry 2")
    ) = CreateResponseModel(
        userId = userId,
        systematicStudyId = systematicStudyId,
        sessionId = sessionId,
        invalidEntries = invalidEntries
    )
}