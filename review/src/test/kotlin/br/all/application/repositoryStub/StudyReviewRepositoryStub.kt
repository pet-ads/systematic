package br.all.application.repositoryStub

import br.all.application.study.repository.StudyReviewDto
import br.all.application.study.repository.StudyReviewRepository
import java.util.*

class StudyReviewRepositoryStub : StudyReviewRepository{
    override fun create(studyReviewDto: StudyReviewDto) {
        TODO("Not yet implemented")
    }

    override fun findAllFromReview(reviewId: UUID): List<StudyReviewDto> {
        TODO("Not yet implemented")
    }

    override fun findById(reviewId: UUID, studyId: Long): StudyReviewDto {
        return StudyReviewDto(
            id = studyId,
            reviewId = reviewId,
            title = "Título do Estudo",
            year = 2023,
            authors = "Autor 1, Autor 2",
            venue = "Local do Estudo",
            abstract = "Este é o resumo do estudo.",
            keywords = setOf("Palavra-chave 1", "Palavra-chave 2"),
            references = listOf("Referência 1", "Referência 2"),
            doi = "DOI do Estudo",
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