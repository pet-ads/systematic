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
class CheckAvailableStudiesToAssignServiceImplTest {
    @MockK
    private lateinit var studyReviewRepository: StudyReviewRepository
    @MockK(relaxUnitFun = true)
    private lateinit var reviewModeRepository: ReviewModeRepository
    @MockK(relaxUnitFun = true)
    private lateinit var reviewerStudyRepository: ReviewerStudyRepository
    @MockK
    private lateinit var authorizationService: AuthorizationService
    @MockK
    private lateinit var authorizationContext: AuthorizationService.AuthorizationContext
    @MockK(relaxed = true)
    private lateinit var presenter: CheckAvailableStudiesToAssignPresenter

    @InjectMockKs
    private lateinit var sut: CheckAvailableStudiesToAssignServiceImpl

    private lateinit var factory: TestDataFactory
    private lateinit var existingReviewerStudies: List<ReviewerStudyDto>
    private lateinit var studies: List<StudyReviewDto>

    private val researcherId = UUID.randomUUID()
    private val systematicStudyId = UUID.randomUUID()

    private val request = CheckAvailableStudiesToAssignService.RequestModel(
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
    @DisplayName("When checking available studies")
    inner class WhenCheckingAvailableStudies {
        @BeforeEach
        fun setUp() {
            every {
                authorizationService.authorize(presenter, researcherId, systematicStudyId, setOf(Role.OWNER))
            } returns authorizationContext
            every { reviewModeRepository.findById(systematicStudyId) } returns reviewModeDto
            every { studyReviewRepository.findAllFromReview(systematicStudyId) } returns studies
        }

        @Test
        fun `should return true when there are studies not yet assigned on selection stage`() {
            every {
                reviewerStudyRepository.findAllByIdSystematicStudyIdAndIdStage(systematicStudyId, StudyReviewStage.SELECTION)
            } returns emptyList()

            sut.check(presenter, withSelectionRequest)

            verify(exactly = 1) {
                presenter.prepareSuccessView(match { it.hasAvailableStudies })
            }
            verify(exactly = 0) { presenter.prepareFailView(any()) }
        }

        @Test
        fun `should return true when there are studies included in selection not yet assigned on extraction stage`() {
            val included = listOf(
                factory.generateDto(studyReviewId = 1L, selectionStatus = "INCLUDED"),
                factory.generateDto(studyReviewId = 3L, selectionStatus = "INCLUDED"),
            )
            every { reviewModeRepository.findById(systematicStudyId) } returns reviewModeDto.copy(extraction = extractionDto)
            every { studyReviewRepository.findAllIncludedInSelection(systematicStudyId) } returns included
            every {
                reviewerStudyRepository.findAllByIdSystematicStudyIdAndIdStage(systematicStudyId, StudyReviewStage.EXTRACTION)
            } returns listOf(
                factory.generateReviewerStudyDto(
                    studyId = 1L, researcherId = reviewers[0].value, stage = StudyReviewStage.EXTRACTION
                )
            )

            sut.check(presenter, withExtractionRequest)

            verify(exactly = 1) { studyReviewRepository.findAllIncludedInSelection(systematicStudyId) }
            verify(exactly = 0) { studyReviewRepository.findAllFromReview(any()) }
            verify(exactly = 1) {
                presenter.prepareSuccessView(match { it.hasAvailableStudies })
            }
            verify(exactly = 0) { presenter.prepareFailView(any()) }
        }

        @Test
        fun `should return false when all studies are already assigned on selection stage`() {
            every { studyReviewRepository.findAllFromReview(systematicStudyId) } returns studies.take(2)
            every {
                reviewerStudyRepository.findAllByIdSystematicStudyIdAndIdStage(systematicStudyId, StudyReviewStage.SELECTION)
            } returns existingReviewerStudies

            sut.check(presenter, withSelectionRequest)

            verify(exactly = 1) {
                presenter.prepareSuccessView(match { !it.hasAvailableStudies })
            }
            verify(exactly = 0) { presenter.prepareFailView(any()) }
        }

        @Test
        fun `should return false when all included studies are already assigned on extraction stage`() {
            val included = listOf(
                factory.generateDto(studyReviewId = 1L, selectionStatus = "INCLUDED"),
            )
            every { reviewModeRepository.findById(systematicStudyId) } returns reviewModeDto.copy(extraction = extractionDto)
            every { studyReviewRepository.findAllIncludedInSelection(systematicStudyId) } returns included
            every {
                reviewerStudyRepository.findAllByIdSystematicStudyIdAndIdStage(systematicStudyId, StudyReviewStage.EXTRACTION)
            } returns listOf(
                factory.generateReviewerStudyDto(
                    studyId = 1L, researcherId = reviewers[0].value, stage = StudyReviewStage.EXTRACTION
                )
            )

            sut.check(presenter, withExtractionRequest)

            verify(exactly = 1) {
                presenter.prepareSuccessView(match { !it.hasAvailableStudies })
            }
            verify(exactly = 0) { presenter.prepareFailView(any()) }
        }

        @Test
        fun `should return false when there are no studies in review`() {
            every { studyReviewRepository.findAllFromReview(systematicStudyId) } returns emptyList()

            sut.check(presenter, withSelectionRequest)

            verify(exactly = 1) {
                presenter.prepareSuccessView(match { !it.hasAvailableStudies })
            }
            verify(exactly = 0) { reviewerStudyRepository.findAllByIdSystematicStudyIdAndIdStage(any(), any()) }
            verify(exactly = 0) { presenter.prepareFailView(any()) }
        }

        @Test
        fun `should return false when no studies were included in selection on extraction stage`() {
            every { reviewModeRepository.findById(systematicStudyId) } returns reviewModeDto.copy(extraction = extractionDto)
            every { studyReviewRepository.findAllIncludedInSelection(systematicStudyId) } returns emptyList()

            sut.check(presenter, withExtractionRequest)

            verify(exactly = 1) {
                presenter.prepareSuccessView(match { !it.hasAvailableStudies })
            }
            verify(exactly = 0) { reviewerStudyRepository.findAllByIdSystematicStudyIdAndIdStage(any(), any()) }
            verify(exactly = 0) { presenter.prepareFailView(any()) }
        }

        @Test
        fun `should not consider studies marked as duplicated in selection`() {
            val selectionStudies = listOf(
                factory.generateDto(studyReviewId = 1L),
                factory.generateDto(studyReviewId = 2L),
                factory.generateDto(studyReviewId = 3L, selectionStatus = "DUPLICATED"),
            )
            every { studyReviewRepository.findAllFromReview(systematicStudyId) } returns selectionStudies
            every {
                reviewerStudyRepository.findAllByIdSystematicStudyIdAndIdStage(systematicStudyId, StudyReviewStage.SELECTION)
            } returns existingReviewerStudies

            sut.check(presenter, withSelectionRequest)

            verify(exactly = 1) {
                presenter.prepareSuccessView(match { !it.hasAvailableStudies })
            }
        }

        @Test
        fun `should not consider studies marked as duplicated in extraction`() {
            val extractionStudies = listOf(
                factory.generateDto(studyReviewId = 1L, selectionStatus = "INCLUDED", extractionStatus = "DUPLICATED"),
                factory.generateDto(studyReviewId = 2L, selectionStatus = "INCLUDED"),
            )
            every { reviewModeRepository.findById(systematicStudyId) } returns reviewModeDto.copy(extraction = extractionDto)
            every { studyReviewRepository.findAllIncludedInSelection(systematicStudyId) } returns extractionStudies
            every {
                reviewerStudyRepository.findAllByIdSystematicStudyIdAndIdStage(systematicStudyId, StudyReviewStage.EXTRACTION)
            } returns listOf(
                factory.generateReviewerStudyDto(
                    studyId = 2L, researcherId = reviewers[0].value, stage = StudyReviewStage.EXTRACTION
                )
            )

            sut.check(presenter, withExtractionRequest)

            verify(exactly = 1) {
                presenter.prepareSuccessView(match { !it.hasAvailableStudies })
            }
        }

        @Test
        fun `should not look for included studies when the stage is selection`() {
            every {
                reviewerStudyRepository.findAllByIdSystematicStudyIdAndIdStage(systematicStudyId, StudyReviewStage.SELECTION)
            } returns existingReviewerStudies

            sut.check(presenter, withSelectionRequest)

            verify(exactly = 0) { studyReviewRepository.findAllIncludedInSelection(any()) }
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
        fun `should not proceed when authorization fails`() {
            every {
                authorizationService.authorize(presenter, researcherId, systematicStudyId, setOf(Role.OWNER))
            } returns null

            sut.check(presenter, request)

            verify(exactly = 0) { reviewModeRepository.findById(any()) }
        }

        @Test
        fun `should prepare fail view when review mode of systematic study is not found`() {
            every { reviewModeRepository.findById(systematicStudyId) } returns null

            sut.check(presenter, request)

            verify(exactly = 1) { presenter.prepareFailView(any<EntityNotFoundException>()) }
        }

        @Test
        fun `should prepare fail view when selection stage is not configured`() {
            every { reviewModeRepository.findById(systematicStudyId) } returns withoutSelectionDto

            sut.check(presenter, withSelectionRequest)

            verify(exactly = 1) { presenter.prepareFailView(any<IllegalStateException>()) }
        }

        @Test
        fun `should prepare fail view when extraction stage is not configured`() {
            every { reviewModeRepository.findById(systematicStudyId) } returns withoutExtractionDto

            sut.check(presenter, withExtractionRequest)

            verify(exactly = 1) { presenter.prepareFailView(any<IllegalStateException>()) }
        }
    }
}
