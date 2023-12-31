package br.all.application.study.shared

import br.all.application.repositoryFake.StudyReviewRepositoryFake
import br.all.application.study.repository.StudyReviewDto
import br.all.application.study.update.interfaces.UpdateStudyReviewStatusService.RequestModel
import java.util.*

fun createStudyReviewDto(uuid: UUID, studyId: Long, repository: StudyReviewRepositoryFake): StudyReviewDto{
    val studyReviewDto = generateStudyReview(uuid, studyId)
    repository.saveOrUpdate(studyReviewDto)
    return studyReviewDto
}

fun createRequestModel(researcherId: UUID, reviewId: UUID, studyId: Long, status: String): RequestModel {
    return RequestModel(
        researcherId,
        reviewId,
        studyId,
        status
    )
}

fun generateStudyReview(reviewId: UUID, studyId: Long): StudyReviewDto{
    return StudyReviewDto(
        studyReviewId = studyId,
        systematicStudyId = reviewId,
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
        criteria = mapOf("Critério 1" to "INCLUSION", "Critério 2" to "EXCLUSION"),
        formAnswers = mapOf(
            UUID.randomUUID() to "Resposta 1",
            UUID.randomUUID() to "Resposta 2"
        ),
        robAnswers = mapOf(
            UUID.randomUUID() to "Qualidade 1",
            UUID.randomUUID() to "Qualidade 2"
        ),
        comments = "Comentários sobre o estudo",
        readingPriority = "LOW",
        extractionStatus = "UNCLASSIFIED",
        selectionStatus = "UNCLASSIFIED"
    )
}