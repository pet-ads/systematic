package br.all.application.study.update

import br.all.application.repositoryStub.StudyReviewRepositoryFake
import br.all.application.study.repository.StudyReviewDto
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.extension.ExtendWith
import java.util.*

@ExtendWith(MockKExtension::class)
class UpdateStudyReviewServiceTest {

    private lateinit var repository: StudyReviewRepositoryFake

    private lateinit var sut: UpdateStudyReviewSelectionService

    @BeforeEach
    fun setUp() {
        repository = StudyReviewRepositoryFake()
        sut = UpdateStudyReviewSelectionService(repository)
    }

    @Test
    @DisplayName("Should change selection status on update: UNCLASSIFIED -> INCLUDED")
    fun shouldChangeSelectionStatusOnUpdate() {
        val uuid = UUID.randomUUID()
        val studyId = 1L
        val studyReviewDto = generateStudyReview(uuid, studyId)
        repository.create(studyReviewDto)
        val requestModel = UpdateStudyReviewRequestModel(
            uuid,
            1L,
            "INCLUDED"
        )

        val response = sut.changeStatus(requestModel)
        val updatedStudyReview = repository.findById(response.reviewId, response.id)

        assertNotEquals(studyReviewDto.selectionStatus, updatedStudyReview?.selectionStatus)
    }

    @Test
    @DisplayName("Should change extraction status on update: UNCLASSIFIED -> INCLUDED")
    fun shouldChangeExtractionStatusOnUpdate() {
        val uuid = UUID.randomUUID()
        val studyId = 1L
        val status = "included"

        val studyReviewDto = createStudyReviewDto(uuid, studyId)
        val requestModel = createRequestModel(uuid, studyId, status)

        val response = sut.changeStatus(requestModel)
        val updatedStudyReview = repository.findById(response.reviewId, response.id)

        assertNotEquals(studyReviewDto.extractionStatus, updatedStudyReview?.extractionStatus)
    }

    private fun createStudyReviewDto(uuid: UUID, studyId: Long): StudyReviewDto{
        val studyReviewDto = generateStudyReview(uuid, studyId)
        repository.create(studyReviewDto)

        return studyReviewDto
    }

    private fun createRequestModel(uuid: UUID, studyId: Long, status: String): UpdateStudyReviewRequestModel{
        return UpdateStudyReviewRequestModel(
            uuid,
            studyId,
            status
        )
    }

    private fun generateStudyReview(reviewId: UUID, studyId: Long): StudyReviewDto{
            return StudyReviewDto(
                id = studyId,
                reviewId = reviewId,
                studyType = "ARTICLE",
                title = "Título do Estudo",
                year = 2023,
                authors = "Autor 1, Autor 2",
                venue = "Local do Estudo",
                abstract = "Este é o resumo do estudo.",
                keywords = setOf("Palavra-chave 1", "Palavra-chave 2"),
                references = listOf("Referência 1", "Referência 2"),
                doi = "https://doi.org/10.1109/5.771073",
                searchSources = setOf("Fonte 1", "Fonte 2"),
                criteria = setOf("Critério 1", "Critério 2"),
                formAnswers = mapOf(
                    UUID.randomUUID() to "Resposta 1",
                    UUID.randomUUID() to "Resposta 2"
                ),
                qualityAnswers = mapOf(
                    UUID.randomUUID() to "Qualidade 1",
                    UUID.randomUUID() to "Qualidade 2"
                ),
                comments = "Comentários sobre o estudo",
                readingPriority = "LOW",
                extractionStatus = "UNCLASSIFIED",
                selectionStatus = "UNCLASSIFIED"
            )
    }

}