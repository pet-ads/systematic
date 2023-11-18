package br.all.application.study.create

import br.all.application.study.create.CreateStudyReviewService.RequestModel
import br.all.application.study.repository.StudyReviewRepository
import br.all.application.study.repository.fromStudyRequestModel
import br.all.application.study.repository.toDto
import br.all.domain.model.study.StudyReview
import br.all.domain.services.IdGeneratorService
import org.junit.jupiter.api.Test
import io.mockk.*
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.extension.ExtendWith
import java.util.*
import kotlin.test.assertEquals

@ExtendWith(MockKExtension::class)
class CreateStudyReviewServiceImplTest {

    @MockK private lateinit var repository: StudyReviewRepository
    @MockK private lateinit var idGenerator: IdGeneratorService
    @MockK private lateinit var presenter: CreateStudyReviewPresenter
    private lateinit var sut: CreateStudyReviewServiceImpl

    @BeforeEach
    fun setUp() {
        sut = CreateStudyReviewServiceImpl(repository, presenter, idGenerator)
    }

    @Disabled
    @Test
    fun `Should create study`() {
        val requestModel = RequestModel(
            "ARTICLE",
            "Title",
            2020,
            "Authors",
            "Journal",
            "abstract",
            emptySet(),
            "source"
        )
        val studyId = 1L
        val reviewId = UUID.randomUUID()
        val dto = StudyReview.fromStudyRequestModel(reviewId, studyId, requestModel).toDto()

        every { idGenerator.next() } returns studyId
        every { repository.create(dto) } returns Unit
        every { repository.findById(reviewId, studyId) } returns dto

        sut.createFromStudy(reviewId, requestModel)
    }
}