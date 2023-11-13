package br.all.application.study.update

import br.all.application.repositoryStub.StudyReviewRepositoryStub
import br.all.application.study.update.UpdateStudyReviewSelectionService.RequestModel
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.extension.ExtendWith
import java.util.*

@ExtendWith(MockKExtension::class)
class UpdateStudyReviewSelectionServiceImplTest {
    @MockK
    private lateinit var repository: StudyReviewRepositoryStub
    @InjectMockKs
    private lateinit var sut: UpdateStudyReviewSelectionServiceImpl

    @BeforeEach
    fun setUp() {
        sut = UpdateStudyReviewSelectionServiceImpl(repository)
    }

    //TODO FIX THIS TEST AND REMOVE DISABLED ANNOTATION
    @Disabled
    @Test
    @DisplayName("Should change selection status on update: UNCLASSIFIED -> INCLUDED")
    fun shouldChangeSelectionStatusOnUpdate() {
        //Given
        val uuid = UUID.randomUUID()
        val studyReviewDto = repository.findById(uuid, 1L)
        val requestModel = RequestModel(
            uuid,
            1L,
            "INCLUDED"
        )
        //When
        val updatedStudyReviewDto = sut.changeStatus(requestModel)
        //Then
        //assertNotEquals(studyReviewDto.selectionStatus, updatedStudyReviewDto.selectionStatus)

    }

}