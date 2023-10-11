package br.all.application.study.update

import br.all.application.repositoryStub.StudyReviewRepositoryStub
import br.all.application.study.create.CreateStudyReviewService
import br.all.application.study.create.StudyReviewRequestModel
import br.all.application.study.repository.StudyReviewDto
import br.all.application.study.repository.StudyReviewRepository
import br.all.application.study.repository.fromStudyRequestModel
import br.all.application.study.repository.toDto
import br.all.domain.model.study.StudyReview
import br.all.domain.services.IdGeneratorService
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.extension.ExtendWith
import kotlin.test.asserter
import org.mockito.Mockito.`when`
import java.util.*

@ExtendWith(MockKExtension::class)
class UpdateStudyReviewSelectionServiceTest {
    @MockK
    private lateinit var repository: StudyReviewRepositoryStub
    @InjectMockKs
    private lateinit var sut: UpdateStudyReviewSelectionService

    @BeforeEach
    fun setUp() {
        sut = UpdateStudyReviewSelectionService(repository)
    }

    @Test
    @DisplayName("Should change selection status on update: UNCLASSIFIED -> INCLUDED")
    fun shouldChangeSelectionStatusOnUpdate() {
        //Given
        val uuid = UUID.randomUUID()
        val studyReviewDto = repository.findById(uuid, 1L)
        val requestModel = UpdateStudyReviewRequestModel(
            uuid,
            1L,
            "INCLUDED"
        )

        //When
        val updatedStudyReviewDto = sut.changeStatus(requestModel)

        //Then
        assertNotEquals(studyReviewDto.selectionStatus, updatedStudyReviewDto.selectionStatus)

    }

}