package br.all.application.study.create

import br.all.application.researcher.credentials.ResearcherCredentialsService
import br.all.application.review.repository.SystematicStudyRepository
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

@ExtendWith(MockKExtension::class)
class CreateStudyReviewServiceImplTest {

    @MockK private lateinit var studyReviewRepository: StudyReviewRepository
    @MockK private lateinit var systematicStudyRepository: SystematicStudyRepository
    @MockK private lateinit var idGenerator: IdGeneratorService
    @MockK private lateinit var credentialService: ResearcherCredentialsService
    @MockK private lateinit var presenter: CreateStudyReviewPresenter
    private lateinit var sut: CreateStudyReviewServiceImpl

    @BeforeEach
    fun setUp() {
        sut = CreateStudyReviewServiceImpl(
            systematicStudyRepository,
            studyReviewRepository,
            presenter,
            credentialService,
            idGenerator
        )
    }

//    @Disabled
//    @Test
//    fun `Should create study`() {
//        val requestModel = RequestModel(
//            UUID.randomUUID(),
//            UUID.randomUUID(),
//            "ARTICLE",
//            "Title",
//            2020,
//            "Authors",
//            "Journal",
//            "abstract",
//            emptySet(),
//            "source"
//        )
//        val studyId = 1L
//        val reviewId = UUID.randomUUID()
//        val dto = StudyReview.fromStudyRequestModel(studyId, requestModel).toDto()
//
//        every { idGenerator.next() } returns studyId
//        every { studyReviewRepository.create(dto) } returns Unit
//        every { studyReviewRepository.findById(reviewId, studyId) } returns dto
//
//        sut.createFromStudy(requestModel)
//    }
}