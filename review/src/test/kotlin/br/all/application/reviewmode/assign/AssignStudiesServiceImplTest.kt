package br.all.application.reviewmode.assign

import br.all.application.reviewmode.repository.ReviewModeDto
import br.all.application.reviewmode.repository.ReviewModeRepository
import br.all.application.reviewmode.repository.ReviewerStudyDto
import br.all.application.reviewmode.repository.ReviewerStudyRepository
import br.all.application.reviewmode.repository.toDto
import br.all.application.shared.service.AuthorizationService
import br.all.application.study.repository.StudyReviewDto
import br.all.application.study.repository.StudyReviewRepository
import br.all.application.study.util.TestDataFactory
import br.all.domain.model.reviewmode.Replication
import br.all.domain.model.reviewmode.Split
import br.all.domain.model.study.StudyReviewStage
import br.all.domain.shared.exception.EntityNotFoundException
import br.all.domain.shared.user.ResearcherId
import br.all.domain.shared.user.Role
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.UUID

@ExtendWith(MockKExtension::class)
class AssignStudiesServiceImplTest {
    @MockK(relaxed = true)
    private lateinit var reviewModeRepository: ReviewModeRepository
    @MockK
    private lateinit var studyReviewRepository: StudyReviewRepository
    @MockK
    private lateinit var reviewerStudyRepository: ReviewerStudyRepository
    @MockK
    private lateinit var authorizationService: AuthorizationService
    @MockK
    private lateinit var authorizationContext: AuthorizationService.AuthorizationContext
    @MockK(relaxed = true)
    private lateinit var presenter: AssignStudiesPresenter

    @InjectMockKs
    private lateinit var sut: AssignStudiesServiceImpl

    private lateinit var factory: TestDataFactory
    private lateinit var existingReviewerStudies: List<ReviewerStudyDto>
    private lateinit var studies: List<StudyReviewDto>

    private val researcherId = UUID.randomUUID()
    private val systematicStudyId = UUID.randomUUID()

    private val request = AssignStudiesService.RequestModel(
        userId = researcherId,
        systematicStudyId = systematicStudyId,
        stage = StudyReviewStage.SELECTION,
    )

    private val withSelectionRequest = request.copy(stage = StudyReviewStage.SELECTION)
    private val withExtractionRequest = request.copy(stage = StudyReviewStage.EXTRACTION)

    private val reviewers = List(3) { ResearcherId(UUID.randomUUID()) }

    private val selectionDto = Split(reviewers, reviewPerStudy = 2).toDto()

    private val extractionDto = Replication(
        mainReviewer = ResearcherId(researcherId),
        secondaryReviewers = mapOf(reviewers.first() to 50.0),
    ).toDto()

    private val reviewModeDto = ReviewModeDto(
        systematicStudyId,
        selection = selectionDto,
        extraction = extractionDto,
    )

    private val withoutSelectionDto = reviewModeDto.copy(selection = null)
    private val withoutExtractionDto = reviewModeDto.copy(extraction = null)


    @BeforeEach
    fun setUp() {
        factory = TestDataFactory()

        studies = List(5) { factory.generateDto(studyReviewId = it + 1L) }

        existingReviewerStudies = listOf(
            factory.generateReviewerStudyDto(studyId = 1L, researcherId = reviewers[0].value),
            factory.generateReviewerStudyDto(studyId = 1L, researcherId = reviewers[1].value),
            factory.generateReviewerStudyDto(studyId = 2L, researcherId = reviewers[2].value),
        )
    }


    @Nested
    @Tag("ValidClasses")
    @DisplayName("When assigning studies")
    inner class WhenAssigningStudies {
        @BeforeEach
        fun setUp() {
            every {
                authorizationService.authorize(presenter, researcherId, systematicStudyId, setOf(Role.OWNER))
            } returns authorizationContext

            every {
                reviewerStudyRepository.findAllBySystematicStudyIdAndStage(systematicStudyId, StudyReviewStage.SELECTION)
            } returns existingReviewerStudies
        }


    }

    @Nested
    @Tag("InvalidClasses")
    @DisplayName("When preconditions fail")
    inner class WhenPreconditionsFail {
        @BeforeEach
        fun setUp() {
            every {
                authorizationService.authorize(presenter, researcherId, systematicStudyId, setOf(Role.OWNER))
            } returns authorizationContext
        }

        @Test
        fun `should not look for the review mode when authorization fails`() {
            every {
                authorizationService.authorize(presenter, researcherId, systematicStudyId, setOf(Role.OWNER))
            } returns null

            sut.assign(presenter, request)

            verify(exactly = 0) { reviewModeRepository.findById(any()) }
        }

        @Test
        fun `should prepare fail view when the review mode of the systematic study is not found`() {
            every { reviewModeRepository.findById(systematicStudyId) } returns null

            sut.assign(presenter, request)

            verify(exactly = 1) { presenter.prepareFailView(any<EntityNotFoundException>()) }
        }

        @Test
        fun `should prepare fail view when the selection stage is not configured`() {
            every { reviewModeRepository.findById(systematicStudyId) } returns
                    withoutSelectionDto

            sut.assign(presenter, withSelectionRequest)

            verify(exactly = 1) { presenter.prepareFailView(any<IllegalStateException>()) }
        }

        @Test
        fun `should prepare fail view when the extraction stage is not configured`() {
            every { reviewModeRepository.findById(systematicStudyId) } returns
                    withoutExtractionDto

            sut.assign(presenter, withExtractionRequest)

            verify(exactly = 1) { presenter.prepareFailView(any<IllegalStateException>()) }
        }

        @Test
        fun `should prepare fail view when there are no studies to assign`() {
            every { reviewModeRepository.findById(systematicStudyId) } returns reviewModeDto
            every { studyReviewRepository.findAllFromReview(systematicStudyId) } returns emptyList()

            sut.assign(presenter, request)

            verify(exactly = 1) { presenter.prepareFailView(any<IllegalStateException>()) }
        }

        @Test
        fun `should prepare fail view and not save when every study is already assigned`() {
            every { reviewModeRepository.findById(systematicStudyId) } returns reviewModeDto
            every { studyReviewRepository.findAllFromReview(systematicStudyId) } returns studies.take(2)
            every {
                reviewerStudyRepository.findAllBySystematicStudyIdAndStage(systematicStudyId, StudyReviewStage.SELECTION)
            } returns existingReviewerStudies

            sut.assign(presenter, request)

            verify(exactly = 1) { presenter.prepareFailView(any<IllegalStateException>()) }
        }
    }
}