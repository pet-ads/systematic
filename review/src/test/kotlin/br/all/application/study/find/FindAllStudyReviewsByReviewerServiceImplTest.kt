package br.all.application.study.find

import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.reviewmode.repository.ReviewerStudyRepository
import br.all.application.study.find.presenter.FindAllStudyReviewsByReviewerPresenter
import br.all.application.study.find.service.FindAllStudyReviewsByReviewerService.ResponseModel
import br.all.application.study.find.service.FindAllStudyReviewsByReviewerServiceImpl
import br.all.application.study.util.TestDataFactory
import br.all.application.user.CredentialsService
import br.all.application.util.PreconditionCheckerMockingNew
import br.all.domain.model.study.StudyReviewStage
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import java.util.*

@Tag("UnitTest")
@Tag("ServiceTest")
@ExtendWith(MockKExtension::class)
class FindAllStudyReviewsByReviewerServiceImplTest {

    @MockK
    private lateinit var reviewerStudyRepository: ReviewerStudyRepository
    @MockK
    private lateinit var systematicStudyRepository: SystematicStudyRepository
    @MockK
    private lateinit var credentialService: CredentialsService
    @MockK(relaxed = true)
    private lateinit var presenter: FindAllStudyReviewsByReviewerPresenter

    private lateinit var sut: FindAllStudyReviewsByReviewerServiceImpl

    private lateinit var factory: TestDataFactory
    private lateinit var preconditionCheckerMocking: PreconditionCheckerMockingNew
    private lateinit var reviewerId: UUID

    @BeforeEach
    fun setUp() {
        factory = TestDataFactory()
        reviewerId = UUID.randomUUID()
        preconditionCheckerMocking = PreconditionCheckerMockingNew(
            presenter,
            credentialService,
            systematicStudyRepository,
            factory.researcherId,
            factory.systematicStudyId
        )
        sut = FindAllStudyReviewsByReviewerServiceImpl(
            systematicStudyRepository,
            reviewerStudyRepository,
            credentialService,
        )
    }

    @Nested
    @Tag("ValidClasses")
    @DisplayName("When successfully finding study reviews by reviewer")
    inner class WhenSuccessfullyFindingStudyReviewsByReviewer {
        @BeforeEach
        fun setUp() = run { preconditionCheckerMocking.makeEverythingWork() }

        @Test
        fun `should find the studies assigned to the reviewer in the stage`() {
            val request = factory.findAllByReviewerRequestModel(reviewerId, StudyReviewStage.SELECTION)
            val reviewerStudies = List(3) { factory.generateReviewerStudyDto(it + 1L, reviewerId) }

            every {
                reviewerStudyRepository.findAllByReviewerPaged(
                    request.systematicStudyId, reviewerId, StudyReviewStage.SELECTION, any<Pageable>()
                )
            } returns PageImpl(reviewerStudies)

            sut.findAllByReviewer(presenter, request)

            val expected = ResponseModel(
                userId = factory.researcherId,
                systematicStudyId = factory.systematicStudyId,
                reviewerId = reviewerId,
                stage = StudyReviewStage.SELECTION,
                studyReviews = reviewerStudies.map { it.studyReview },
                page = 0,
                size = 20,
                totalElements = 3,
                totalPages = 1
            )
            verify(exactly = 1) { presenter.prepareSuccessView(expected) }
        }

        @Test
        fun `should query all stages when no stage is informed`() {
            val request = factory.findAllByReviewerRequestModel(reviewerId)

            every {
                reviewerStudyRepository.findAllByReviewerPaged(request.systematicStudyId, reviewerId, null, any<Pageable>())
            } returns PageImpl(emptyList())

            sut.findAllByReviewer(presenter, request)

            verify(exactly = 1) { presenter.prepareSuccessView(match { it.stage == null }) }
        }

        @Test
        fun `should succeed with an empty list when the reviewer has no studies`() {
            val request = factory.findAllByReviewerRequestModel(reviewerId)

            every {
                reviewerStudyRepository.findAllByReviewerPaged(request.systematicStudyId, reviewerId, null, any<Pageable>())
            } returns PageImpl(emptyList())

            sut.findAllByReviewer(presenter, request)

            verify(exactly = 1) { presenter.prepareSuccessView(match { it.studyReviews.isEmpty() }) }
        }

        @Test
        fun `should forward page, size and sort to the repository`() {
            val request = factory.findAllByReviewerRequestModel(reviewerId, page = 2, pageSize = 5, sort = "year,desc")
            val pageable = slot<Pageable>()

            every {
                reviewerStudyRepository.findAllByReviewerPaged(request.systematicStudyId, reviewerId, null, capture(pageable))
            } returns PageImpl(emptyList())

            sut.findAllByReviewer(presenter, request)

            assertEquals(2, pageable.captured.pageNumber)
            assertEquals(5, pageable.captured.pageSize)
            assertEquals(Sort.Direction.DESC, pageable.captured.sort.getOrderFor("year")?.direction)
        }
    }

    @Nested
    @Tag("InvalidClasses")
    @DisplayName("When failing to find study reviews by reviewer")
    inner class WhenFailingToFindStudyReviewsByReviewer {
        @Test
        fun `should not be allowed to find studies when unauthenticated`() {
            val request = factory.findAllByReviewerRequestModel(reviewerId)

            preconditionCheckerMocking.testForUnauthenticatedUser(presenter, request) { _, _ ->
                sut.findAllByReviewer(presenter, request)
            }
        }

        @Test
        fun `should not be allowed to find studies when unauthorized`() {
            val request = factory.findAllByReviewerRequestModel(reviewerId)

            preconditionCheckerMocking.testForUnauthorizedUser(presenter, request) { _, _ ->
                sut.findAllByReviewer(presenter, request)
            }
        }

        @Test
        fun `should not be allowed to find studies when systematic study doesn't exist`() {
            val request = factory.findAllByReviewerRequestModel(reviewerId)

            preconditionCheckerMocking.testForNonexistentSystematicStudy(presenter, request) { _, _ ->
                sut.findAllByReviewer(presenter, request)
            }
        }
    }
}