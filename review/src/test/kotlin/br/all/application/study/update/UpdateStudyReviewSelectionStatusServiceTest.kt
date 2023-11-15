package br.all.application.study.update

import br.all.application.repositoryFake.StudyReviewRepositoryFake
import br.all.application.study.shared.createRequestModel
import br.all.application.study.shared.createStudyReviewDto
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.extension.ExtendWith
import java.util.*

class UpdateStudyReviewSelectionStatusServiceTest {

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
        val status = "INCLUDED"

        val studyReviewDto = createStudyReviewDto(uuid, studyId, repository)
        val requestModel = createRequestModel(uuid, studyId, status)

        val response = sut.changeStatus(requestModel)
        val updatedStudyReview = repository.findById(response.reviewId, response.id)

        assertNotEquals(studyReviewDto.selectionStatus, updatedStudyReview?.selectionStatus)
        assertEquals(updatedStudyReview?.selectionStatus, status)
    }

}