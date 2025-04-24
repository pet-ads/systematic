package br.all.report.shared

import br.all.domain.model.question.QuestionContextEnum
import br.all.domain.model.study.StudyType
import br.all.domain.shared.utils.paragraph
import br.all.domain.shared.utils.paragraphList
import br.all.domain.shared.utils.wordsList
import br.all.domain.shared.utils.year
import br.all.infrastructure.question.QuestionDocument
import br.all.infrastructure.study.StudyReviewDocument
import br.all.infrastructure.study.StudyReviewId
import br.all.infrastructure.study.StudyReviewIdGeneratorService
import io.github.serpro69.kfaker.Faker
import java.lang.Math.random
import java.util.*

class TestDataFactory {
    private val faker = Faker()
    private val studyReviewId = random().toLong()

    fun validCreateTextualQuestionDocument(
        questionId: UUID,
        systematicStudyId: UUID,
        code: String = faker.lorem.words(),
        description: String = faker.lorem.words(),
        questionType: QuestionContextEnum

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
        questionType: QuestionContextEnum
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
        questionType: QuestionContextEnum
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
        questionType: QuestionContextEnum
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

    fun reviewDocument(
        systematicStudyId: UUID,
        studyReviewId: Long = this.studyReviewId,
        searchSessionId: UUID = UUID.randomUUID(),
        type: String = faker.random.nextEnum(StudyType::class.java).toString(),
        title: String = faker.book.title(),
        year: Int = faker.year(),
        authors: String = "Marie Curie",
        venue: String = faker.book.publisher(),
        abstract: String = faker.paragraph(20),
        keywords: Set<String> = faker.wordsList(5).toSet(),
        references: List<String> = faker.paragraphList(4, 5),
        doi: String? = null,
        sources: Set<String> = faker.wordsList(minSize = 1, maxSize = 5).toSet(),
        criteria: Set<String> = setOf("Criteria A"),
        formAnswers: Map<UUID, String>,
        robAnswers: Map<UUID, String>,
        comments: String = faker.paragraph(15),
        selectionStatus: String = "UNCLASSIFIED",
        extractionStatus: String = "UNCLASSIFIED",
        readingPriority: String = "LOW",
        score: Int = 0
    ): StudyReviewDocument {
        val studyId = StudyReviewId(systematicStudyId, studyReviewId)
        return StudyReviewDocument(
            studyId, searchSessionId, type, title, year,
            authors, venue, abstract, keywords, references, doi, sources,
            criteria, formAnswers, robAnswers, comments, readingPriority,
            extractionStatus, selectionStatus, score
        )
    }
}