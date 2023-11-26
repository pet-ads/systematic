package br.all.application.study.update

import br.all.application.repositoryFake.StudyReviewRepositoryFake
import br.all.application.study.shared.createStudyReviewDto
import br.all.application.study.update.implementation.UpdateStudyReviewPriorityService
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.util.*

class UpdateStudyReviewReadingPriorityTest {
//    private lateinit var repository: StudyReviewRepositoryFake
//    private lateinit var sut: UpdateStudyReviewPriorityService
//
//    @BeforeEach
//    fun setUp() {
//        repository = StudyReviewRepositoryFake()
//        sut = UpdateStudyReviewPriorityService(repository)
//    }
//
//    @Test
//    @DisplayName("Should change reading priority on update: LOW -> HIGH")
//    fun shouldChangeReadingStatusToHighOnUpdate() {
//        val uuid = UUID.randomUUID()
//        val studyId = 1L
//        val priority = "HIGH"
//
//        val studyReviewDto = createStudyReviewDto(uuid, studyId, repository)
//        val requestModel = UpdateStudyReviewPriorityService.RequestModel(uuid, studyId, priority)
//
//        sut.changeStatus(requestModel)
//        val updatedStudyReview = repository.findById(uuid, studyId)
//
//        Assertions.assertNotEquals(studyReviewDto.readingPriority, updatedStudyReview?.readingPriority)
//        Assertions.assertEquals(updatedStudyReview?.readingPriority, priority)
//    }
//
//    @Test
//    @DisplayName("Should change reading priority on update: LOW -> VERY_HIGH")
//    fun shouldChangeReadingStatusToVeryHighOnUpdate() {
//        val uuid = UUID.randomUUID()
//        val studyId = 1L
//        val priority = "VERY_HIGH"
//
//        val studyReviewDto = createStudyReviewDto(uuid, studyId, repository)
//        val requestModel = UpdateStudyReviewPriorityService.RequestModel(uuid, studyId, priority)
//
//        sut.changeStatus(requestModel)
//        val updatedStudyReview = repository.findById(uuid, studyId)
//
//        Assertions.assertNotEquals(studyReviewDto.readingPriority, updatedStudyReview?.readingPriority)
//        Assertions.assertEquals(updatedStudyReview?.readingPriority, priority)
//    }
//
//    @Test
//    @DisplayName("Should change reading priority on update: LOW -> VERY_LOW")
//    fun shouldChangeReadingStatusToVeryLowOnUpdate() {
//        val uuid = UUID.randomUUID()
//        val studyId = 1L
//        val priority = "VERY_LOW"
//
//        val studyReviewDto = createStudyReviewDto(uuid, studyId, repository)
//        val requestModel = UpdateStudyReviewPriorityService.RequestModel(uuid, studyId, priority)
//
//        sut.changeStatus(requestModel)
//        val updatedStudyReview = repository.findById(uuid, studyId)
//
//        Assertions.assertNotEquals(studyReviewDto.readingPriority, updatedStudyReview?.readingPriority)
//        Assertions.assertEquals(updatedStudyReview?.readingPriority, priority)
//    }

}