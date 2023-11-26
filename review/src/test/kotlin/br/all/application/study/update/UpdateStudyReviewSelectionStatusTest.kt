package br.all.application.study.update

import br.all.application.repositoryFake.StudyReviewRepositoryFake
import br.all.application.study.shared.createStudyReviewDto
import br.all.application.study.update.implementation.UpdateStudyReviewSelectionService
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import java.util.*

class UpdateStudyReviewSelectionStatusTest {

    private lateinit var repository: StudyReviewRepositoryFake
    private lateinit var sut: UpdateStudyReviewSelectionService

//    @BeforeEach
//    fun setUp() {
//        repository = StudyReviewRepositoryFake()
//        sut = UpdateStudyReviewSelectionService(repository)
//    }
//
//    @Test
//    @DisplayName("Should change selection status on update: UNCLASSIFIED -> INCLUDED")
//    fun shouldChangeSelectionStatusToIncludedOnUpdate() {
//        val uuid = UUID.randomUUID()
//        val studyId = 1L
//        val status = "INCLUDED"
//
//        val studyReviewDto = createStudyReviewDto(uuid, studyId, repository)
//        val requestModel = UpdateStudyReviewSelectionService.RequestModel(uuid, studyId, status)
//
//        sut.changeStatus(requestModel)
//        val updatedStudyReview = repository.findById(uuid, studyId)
//
//        assertNotEquals(studyReviewDto.selectionStatus, updatedStudyReview?.selectionStatus)
//        assertEquals(updatedStudyReview?.selectionStatus, status)
//    }
//
//    @Test
//    @DisplayName("Should change selection status on update: UNCLASSIFIED -> DUPLICATED")
//    fun shouldChangeSelectionStatusToDuplicatedOnUpdate() {
//        val uuid = UUID.randomUUID()
//        val studyId = 1L
//        val status = "DUPLICATED"
//
//        val studyReviewDto = createStudyReviewDto(uuid, studyId, repository)
//        val requestModel = UpdateStudyReviewSelectionService.RequestModel(uuid, studyId, status)
//
//        sut.changeStatus(requestModel)
//        val updatedStudyReview = repository.findById(uuid, studyId)
//
//        assertNotEquals(studyReviewDto.selectionStatus, updatedStudyReview?.selectionStatus)
//        assertEquals(updatedStudyReview?.selectionStatus, status)
//    }
//
//    @Test
//    @DisplayName("Should change selection status on update: UNCLASSIFIED -> EXCLUDED")
//    fun shouldChangeSelectionStatusToExcludedOnUpdate() {
//        val uuid = UUID.randomUUID()
//        val studyId = 1L
//        val status = "EXCLUDED"
//
//        val studyReviewDto = createStudyReviewDto(uuid, studyId, repository)
//        val requestModel = UpdateStudyReviewSelectionService.RequestModel(uuid, studyId, status)
//
//        sut.changeStatus(requestModel)
//        val updatedStudyReview = repository.findById(uuid, studyId)
//
//        assertNotEquals(studyReviewDto.selectionStatus, updatedStudyReview?.selectionStatus)
//        assertEquals(updatedStudyReview?.selectionStatus, status)
//    }

}