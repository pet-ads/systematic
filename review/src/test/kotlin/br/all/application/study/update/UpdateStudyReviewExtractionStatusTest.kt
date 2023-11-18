package br.all.application.study.update

import br.all.application.repositoryFake.StudyReviewRepositoryFake
import br.all.application.study.shared.createRequestModel
import br.all.application.study.shared.createStudyReviewDto
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.util.*

class UpdateStudyReviewExtractionStatusTest {

    private lateinit var repository: StudyReviewRepositoryFake

    private lateinit var sut: UpdateStudyReviewExtractionStatusService

    @BeforeEach
    fun setUp() {
        repository = StudyReviewRepositoryFake()
        sut = UpdateStudyReviewExtractionStatusService(repository)
    }

    @Test
    @DisplayName("Should change extraction status on update: UNCLASSIFIED -> INCLUDED")
    fun shouldChangeExtractionStatusToIncludedOnUpdate() {
        val uuid = UUID.randomUUID()
        val studyId = 1L
        val status = "INCLUDED"

        val studyReviewDto = createStudyReviewDto(uuid, studyId, repository)
        val requestModel = createRequestModel(uuid, studyId, status)

        val response = sut.changeStatus(requestModel)
        val updatedStudyReview = repository.findById(response.reviewId, response.id)

        Assertions.assertNotEquals(studyReviewDto.extractionStatus, updatedStudyReview?.extractionStatus)
        Assertions.assertEquals(updatedStudyReview?.selectionStatus, status)
    }

    @Test
    @DisplayName("Should change extraction status on update: UNCLASSIFIED -> DUPLICATED")
    fun shouldChangeExtractionStatusToDuplicatedOnUpdate() {
        val uuid = UUID.randomUUID()
        val studyId = 1L
        val status = "DUPLICATED"

        val studyReviewDto = createStudyReviewDto(uuid, studyId, repository)
        val requestModel = createRequestModel(uuid, studyId, status)

        val response = sut.changeStatus(requestModel)
        val updatedStudyReview = repository.findById(response.reviewId, response.id)

        Assertions.assertNotEquals(studyReviewDto.extractionStatus, updatedStudyReview?.extractionStatus)
        Assertions.assertEquals(updatedStudyReview?.selectionStatus, status)
    }

    @Test
    @DisplayName("Should change extraction status on update: UNCLASSIFIED -> EXCLUDED")
    fun shouldChangeExtractionStatusToExcludedOnUpdate() {
        val uuid = UUID.randomUUID()
        val studyId = 1L
        val status = "EXCLUDED"

        val studyReviewDto = createStudyReviewDto(uuid, studyId, repository)
        val requestModel = createRequestModel(uuid, studyId, status)

        val response = sut.changeStatus(requestModel)
        val updatedStudyReview = repository.findById(response.reviewId, response.id)

        Assertions.assertNotEquals(studyReviewDto.extractionStatus, updatedStudyReview?.extractionStatus)
        Assertions.assertEquals(updatedStudyReview?.selectionStatus, status)
    }

}