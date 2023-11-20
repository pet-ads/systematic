package br.all.application.study.shared

import br.all.application.repositoryFake.StudyReviewRepositoryFake
import br.all.application.study.repository.StudyReviewDto
import br.all.application.study.update.UpdateStudyReviewRequestModel
import java.util.*

fun createStudyReviewDto(uuid: UUID, studyId: Long, repository: StudyReviewRepositoryFake): StudyReviewDto{
    val studyReviewDto = generateStudyReview(uuid, studyId)
    repository.saveOrUpdate(studyReviewDto)
    return studyReviewDto
}

fun createRequestModel(uuid: UUID, studyId: Long, status: String): UpdateStudyReviewRequestModel{
    return UpdateStudyReviewRequestModel(
        uuid,
        studyId,
        status
    )
}

fun generateStudyReview(reviewId: UUID, studyId: Long): StudyReviewDto{
    return StudyReviewDto(
        reviewId = reviewId,
        studyId = studyId,
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