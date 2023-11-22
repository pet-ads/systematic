package br.all.study.utils

import br.all.application.study.create.CreateStudyReviewService
import br.all.application.study.update.interfaces.UpdateStudyReviewStatusService
import br.all.infrastructure.study.StudyReviewDocument
import br.all.infrastructure.study.StudyReviewId
import java.util.*

class TestDataFactory {

    val researcherId: UUID = UUID.randomUUID()
    val systematicStudyId: UUID = UUID.randomUUID()

    fun validPostRequest() = CreateStudyReviewService.RequestModel(
        researcherId,
        systematicStudyId,
        "ARTICLE",
        "Title",
        2021,
        "Authors",
        "Journal",
        "abstract",
        emptySet(),
        "source"
    )

    fun validStatusUpdatePatchRequest(id: Long, newStatus: String) =
        UpdateStudyReviewStatusService.RequestModel(researcherId, systematicStudyId, id, newStatus)

    fun reviewDocument(
        reviewId: UUID,
        studyId: Long,
        studyTitle: String = "study",
        selectionStatus: String = "UNCLASSIFIED",
        extractionStatus: String = "UNCLASSIFIED",
        readingPriority: String = "LOW"
    ): StudyReviewDocument {
        val studyReviewId = StudyReviewId(reviewId, studyId)
        return StudyReviewDocument(
            studyReviewId,
            "ARTICLE",
            studyTitle,
            2023,
            "Lucas",
            "JSS",
            "Mussum Ipsum, cacilds vidis litro abertis. Admodum accumsan disputationi eu sit.",
            setOf("PET", "IFSP"),
            emptyList(),
            null,
            setOf("Scopus", "Springer"),
            setOf("Criteria A", "Criteria B"),
            mapOf(Pair(UUID.randomUUID(), "Form")),
            mapOf(Pair(UUID.randomUUID(), "Quality")),
            "",
            readingPriority,
            extractionStatus,
            selectionStatus
        )
    }
}