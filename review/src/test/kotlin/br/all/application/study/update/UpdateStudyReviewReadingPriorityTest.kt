package br.all.application.study.update

import br.all.application.repositoryFake.StudyReviewRepositoryFake
import br.all.application.study.shared.createRequestModel
import br.all.application.study.shared.createStudyReviewDto
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.util.*

class UpdateStudyReviewReadingPriorityTest {
    private lateinit var repository: StudyReviewRepositoryFake

    private lateinit var sut: UpdateStudyReviewReadingPriorityService

    @BeforeEach
    fun setUp() {
        repository = StudyReviewRepositoryFake()
        sut = UpdateStudyReviewReadingPriorityService(repository)
    }

    @Test
    @DisplayName("Should change reading priority on update: LOW -> HIGH")
    fun shouldChangeReadingStatusToHighOnUpdate() {
        val uuid = UUID.randomUUID()
        val studyId = 1L
        val priority = "HIGH"

        val studyReviewDto = createStudyReviewDto(uuid, studyId, repository)
        val requestModel = createRequestModel(uuid, studyId, priority)

        val response = sut.changeStatus(requestModel)
        val updatedStudyReview = repository.findById(response.reviewId, response.id)

        Assertions.assertNotEquals(studyReviewDto.readingPriority, updatedStudyReview?.readingPriority)
        Assertions.assertEquals(updatedStudyReview?.readingPriority, priority)
    }

    @Test
    @DisplayName("Should change reading priority on update: LOW -> VERY_LOW")
    fun shouldChangeReadingStatusToVeryLowOnUpdate() {
        val uuid = UUID.randomUUID()
        val studyId = 1L
        val priority = "VERY_LOW"

        val studyReviewDto = createStudyReviewDto(uuid, studyId, repository)
        val requestModel = createRequestModel(uuid, studyId, priority)

        val response = sut.changeStatus(requestModel)
        val updatedStudyReview = repository.findById(response.reviewId, response.id)

        Assertions.assertNotEquals(studyReviewDto.readingPriority, updatedStudyReview?.readingPriority)
        Assertions.assertEquals(updatedStudyReview?.readingPriority, priority)
    }

}