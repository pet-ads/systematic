package br.all.application.study.util

import br.all.application.study.create.CreateStudyReviewService
import br.all.application.study.find.service.FindStudyReviewService
import br.all.application.study.repository.StudyReviewDto
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
    private val faker = Faker()

    fun generateDto(
        systematicStudyId: UUID = this.systematicStudyId,
        studyReviewId: Long = this.studyReviewId,
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
        criteria: Map<String,String> = mapOf("Criteria A" to "INCLUSION", "Criteria B" to "EXCLUSION"),
        formAnswers: Map<UUID, String> = mapOf(Pair(UUID.randomUUID(), "Form")),
        robAnswers: Map<UUID, String> = mapOf(Pair(UUID.randomUUID(), "Form")),
        comments: String = faker.paragraph(15),
        selectionStatus: String = "UNCLASSIFIED",
        extractionStatus: String = "UNCLASSIFIED",
        readingPriority: String = "LOW"
    ): StudyReviewDto {
        return StudyReviewDto(
            studyReviewId, systematicStudyId, type, title, year,
            authors, venue, abstract, keywords, references, doi, sources,
            criteria, formAnswers, robAnswers, comments, readingPriority,
            extractionStatus, selectionStatus
        )
    }

    fun createRequestModel(
        researcherId: UUID = this.researcherId,
        systematicStudyId: UUID = this.systematicStudyId,
        type: String = faker.random.nextEnum(StudyType::class.java).toString(),
        title: String = faker.book.title(),
        year: Int = faker.year(),
        authors: String = faker.book.author(),
        venue: String = faker.lorem.words(),
        abstract: String = faker.lorem.words(),
        keywords: Set<String> = setOf(faker.lorem.words(), faker.lorem.words()),
        source: String = faker.lorem.words(),
    ) = CreateStudyReviewService.RequestModel(researcherId, systematicStudyId, type, title, year, authors, venue, abstract, keywords, source)

    fun findRequestModel(
    ) = FindStudyReviewService.RequestModel(researcherId, systematicStudyId, studyReviewId)

    fun findResponseModel() = FindStudyReviewService.ResponseModel(researcherId, generateDto())

    operator fun component1() = researcherId
    operator fun component2() = studyReviewId


}