package br.all.application.study.util

import br.all.application.question.repository.QuestionDto
import br.all.application.study.create.CreateStudyReviewService
import br.all.application.study.find.service.FindAllStudyReviewsBySourceService
import br.all.application.study.find.service.FindAllStudyReviewsService
import br.all.application.study.find.service.FindStudyReviewService
import br.all.application.study.repository.StudyReviewDto
import br.all.application.study.update.interfaces.AnswerRiskOfBiasQuestionService
import br.all.application.study.update.interfaces.MarkAsDuplicatedService
import br.all.application.study.update.interfaces.UpdateStudyReviewService
import br.all.application.study.update.interfaces.UpdateStudyReviewStatusService
import br.all.domain.model.question.QuestionContextEnum
import br.all.domain.model.study.StudyType
import br.all.domain.shared.utils.paragraph
import br.all.domain.shared.utils.paragraphList
import br.all.domain.shared.utils.wordsList
import br.all.domain.shared.utils.year
import io.github.serpro69.kfaker.Faker
import java.util.*
import kotlin.random.Random

class TestDataFactory {

    val researcherId: UUID = UUID.randomUUID()
    val studyReviewId: Long = Random(1).nextLong()
    val systematicStudyId: UUID = UUID.randomUUID()
    val searchSessionId: UUID = UUID.randomUUID()
    private val faker = Faker()

    fun generateDto(
        systematicStudyId: UUID = this.systematicStudyId,
        studyReviewId: Long = this.studyReviewId,
        searchSessionId: UUID = this.searchSessionId,
        type: String = faker.random.nextEnum(StudyType::class.java).toString(),
        title: String = faker.book.title(),
        year: Int = faker.year(),
        authors: String = faker.science.scientist(),
        venue: String = faker.book.publisher(),
        abstract: String = faker.paragraph(20),
        keywords: Set<String> = faker.wordsList(5).toSet(),
        references: List<String> = faker.paragraphList(4, 5),
        doi: String? = null,
        sources: Set<String> = faker.wordsList(minSize = 1, maxSize = 5).toSet(),
        criteria: Set<String> = setOf("Criteria A", "Criteria B"),
        formAnswers: Map<UUID, String> = mapOf(Pair(UUID.randomUUID(), "Form")),
        robAnswers: Map<UUID, String> = mapOf(Pair(UUID.randomUUID(), "Form")),
        comments: String = faker.paragraph(15),
        selectionStatus: String = "UNCLASSIFIED",
        extractionStatus: String = "UNCLASSIFIED",
        readingPriority: String = "LOW"
    ): StudyReviewDto {
        return StudyReviewDto(
            studyReviewId, systematicStudyId, searchSessionId, type, title, year,
            authors, venue, abstract, keywords, references, doi, sources,
            criteria, formAnswers, robAnswers, comments, readingPriority,
            extractionStatus, selectionStatus
        )
    }

    fun createRequestModel(
        researcherId: UUID = this.researcherId,
        systematicStudyId: UUID = this.systematicStudyId,
        searchSessionId: UUID = this.searchSessionId,
        type: String = faker.random.nextEnum(StudyType::class.java).toString(),
        title: String = faker.book.title(),
        year: Int = faker.year(),
        authors: String = faker.book.author(),
        venue: String = faker.lorem.words(),
        abstract: String = faker.lorem.words(),
        keywords: Set<String> = setOf(faker.lorem.words(), faker.lorem.words()),
        source: String = faker.lorem.words(),
    ) = CreateStudyReviewService.RequestModel(researcherId, systematicStudyId, searchSessionId, type, title, year, authors, venue, abstract, keywords, source)

    fun findRequestModel(
    ) = FindStudyReviewService.RequestModel(researcherId, systematicStudyId, studyReviewId)

    fun findResponseModel() = FindStudyReviewService.ResponseModel(researcherId, generateDto())

    fun findAllRequestModel(
    ) = FindAllStudyReviewsService.RequestModel(researcherId, systematicStudyId)

    fun findAllResponseModel(
        amountOfStudies: Int,
    ) = FindAllStudyReviewsService.ResponseModel(
        researcherId,
        systematicStudyId,
        List(amountOfStudies) { generateDto(studyReviewId = Random(1).nextLong()) }
    )

    fun findAllBySourceRequestModel(
        searchSource: String
    ) = FindAllStudyReviewsBySourceService.RequestModel(researcherId, systematicStudyId, searchSource)

    fun findAllBySourceResponseModel(
        amountOfStudies: Int,
        searchSource: String,
    ) = FindAllStudyReviewsBySourceService.ResponseModel(
        researcherId,
        systematicStudyId,
        searchSource,
        List(amountOfStudies) { generateDto(studyReviewId = Random(1).nextLong()) }
    )

    fun updateRequestModel(
        researcherId: UUID = this.researcherId,
        systematicStudyId: UUID = this.systematicStudyId,
        searchSessionId: UUID = this.searchSessionId,
        type: String = faker.random.nextEnum(StudyType::class.java).toString(),
        title: String = faker.book.title(),
        year: Int = faker.year(),
        authors: String = faker.book.author(),
        venue: String = faker.lorem.words(),
        abstract: String = faker.lorem.words(),
        keywords: Set<String> = setOf(faker.lorem.words(), faker.lorem.words()),
        source: String = faker.lorem.words(),
        studyReviewId: Long = this.studyReviewId
    ) = UpdateStudyReviewService.RequestModel(researcherId, systematicStudyId, searchSessionId, type, title, year, authors, venue, abstract, keywords, source, studyReviewId)

    fun updateStatusRequestModel(
        status: String,
        criteria: Set<String>
    ) = UpdateStudyReviewStatusService.RequestModel(researcherId, systematicStudyId, listOf(studyReviewId), status, criteria)

    fun markAsDuplicatedRequestModel(
        keptStudyReviewId: Long,
        duplicateIds: List<Long>
    ) = MarkAsDuplicatedService.RequestModel(researcherId, systematicStudyId, keptStudyReviewId, duplicateIds)

    fun <T> answerRequestModel(
        questionId: UUID,
        type: String,
        answer: T,
    ) = AnswerRiskOfBiasQuestionService.RequestModel(researcherId, systematicStudyId, studyReviewId, questionId, type, answer)

    fun labelDto(
        name: String,
        value: Int,
    ) = AnswerRiskOfBiasQuestionService.LabelDto(name, value)

    fun generateQuestionTextualDto(
        questionId: UUID,
        systematicStudyId: UUID = this.systematicStudyId,
        code: String = faker.lorem.words(),
        description: String = faker.lorem.words(),
    ) =
        QuestionDto(
            questionId,
            systematicStudyId,
            code,
            description,
            "TEXTUAL",
            null,
            null,
            null,
            null, QuestionContextEnum.EXTRACTION
        )

    fun generateQuestionLabeledScaleDto(
        questionId: UUID,
        systematicStudyId: UUID = this.systematicStudyId,
        code: String = faker.lorem.words(),
        description: String = faker.lorem.words(),
        labelDto: AnswerRiskOfBiasQuestionService.LabelDto
    ) =
        QuestionDto(
            questionId,
            systematicStudyId,
            code,
            description,
            "LABELED_SCALE",
            mapOf(labelDto.name to labelDto.value),
            null,
            null,
            null, QuestionContextEnum.EXTRACTION
        )

    operator fun component1() = researcherId
    operator fun component2() = studyReviewId


}