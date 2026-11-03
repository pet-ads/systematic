package br.all.application.study.find.service

import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.reviewmode.repository.ReviewerStudySearchRepository
import br.all.application.study.find.presenter.FindAllStudyReviewsByAdvancedSearchPresenter
import br.all.application.study.util.TestDataFactory
import br.all.application.user.CredentialsService
import br.all.application.util.PreconditionCheckerMockingNew
import br.all.domain.model.study.StudyReviewStage
import br.all.domain.shared.exception.UnauthorizedUserException
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Tags
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import java.util.*

@Tags(Tag("UnitTest"), Tag("ServiceTest"))
@ExtendWith(MockKExtension::class)
class FindAllReviewerStudiesBySearchServiceImplTest {

    @MockK private lateinit var systematicStudyRepository: SystematicStudyRepository
    @MockK private lateinit var reviewerStudySearchRepository: ReviewerStudySearchRepository
    @MockK private lateinit var credentialsService: CredentialsService
    @MockK(relaxed = true) private lateinit var presenter: FindAllStudyReviewsByAdvancedSearchPresenter

    @InjectMockKs
    private lateinit var sut: FindAllReviewerStudiesBySearchServiceImpl

    private lateinit var factory: TestDataFactory
    private lateinit var preconditionCheckerMocking: PreconditionCheckerMockingNew

    @BeforeEach
    fun setUp() {
        factory = TestDataFactory()
        preconditionCheckerMocking = PreconditionCheckerMockingNew(
            presenter, credentialsService, systematicStudyRepository, factory.researcherId, factory.systematicStudyId
        )
    }

    @Nested
    @DisplayName("When searching reviewer studies")
    inner class SuccessTests {

        @Test
        fun `should search and return logged reviewer's study copies`() {
            val reviewerId = factory.researcherId
            val copy1 = factory.generateReviewerStudyDto(1L, reviewerId, StudyReviewStage.SELECTION)
            val copy2 = factory.generateReviewerStudyDto(2L, reviewerId, StudyReviewStage.SELECTION)
            val page = PageImpl(listOf(copy1, copy2))

            val request = FindAllReviewerStudiesBySearchService.RequestModel(
                userId = reviewerId,
                systematicStudyId = factory.systematicStudyId,
                reviewerId = reviewerId,
                stage = StudyReviewStage.SELECTION,
                title = "Clean Code",
                page = 0,
                pageSize = 10,
                sort = "id,desc"
            )

            preconditionCheckerMocking.makeEverythingWork()

            val pageableSlot = slot<Pageable>()
            every {
                reviewerStudySearchRepository.findAllByAdvancedSearch(
                    systematicStudyId = factory.systematicStudyId,
                    reviewerId = reviewerId,
                    stage = StudyReviewStage.SELECTION,
                    filters = any(),
                    pageable = capture(pageableSlot)
                )
            } returns page

            sut.findAllBySearch(presenter, request)

            assertEquals(0, pageableSlot.captured.pageNumber)
            assertEquals(10, pageableSlot.captured.pageSize)
            assertEquals(Sort.by(Sort.Direction.DESC, "id"), pageableSlot.captured.sort)

            val responseSlot = slot<FindAllStudyReviewsByAdvancedSearchService.ResponseModel>()
            verify(exactly = 1) { presenter.prepareSuccessView(capture(responseSlot)) }

            assertEquals(2, responseSlot.captured.studyReviews.size)
            assertEquals(2L, responseSlot.captured.totalElements)
        }
    }

    @Nested
    @DisplayName("When unable to search reviewer studies")
    inner class FailureTests {

        @Test
        fun `should fail when reviewer is trying to search another reviewer's copies`() {
            val userId = factory.researcherId
            val otherReviewerId = UUID.randomUUID()
            val request = FindAllReviewerStudiesBySearchService.RequestModel(
                userId = userId,
                systematicStudyId = factory.systematicStudyId,
                reviewerId = otherReviewerId
            )

            preconditionCheckerMocking.makeEverythingWork()

            sut.findAllBySearch(presenter, request)

            verify(exactly = 1) { presenter.prepareFailView(any<UnauthorizedUserException>()) }
            verify(exactly = 0) { reviewerStudySearchRepository.findAllByAdvancedSearch(any(), any(), any(), any(), any()) }
        }

        @Test
        fun `should fail when user is unauthenticated`() {
            val request = FindAllReviewerStudiesBySearchService.RequestModel(
                userId = factory.researcherId,
                systematicStudyId = factory.systematicStudyId,
                reviewerId = factory.researcherId
            )

            preconditionCheckerMocking.makeUserUnauthenticated()
            sut.findAllBySearch(presenter, request)

            verify(exactly = 1) { presenter.prepareFailView(any()) }
        }

        @Test
        fun `should fail when user is unauthorized`() {
            val request = FindAllReviewerStudiesBySearchService.RequestModel(
                userId = factory.researcherId,
                systematicStudyId = factory.systematicStudyId,
                reviewerId = factory.researcherId
            )

            preconditionCheckerMocking.makeUserUnauthorized()
            sut.findAllBySearch(presenter, request)

            verify(exactly = 1) { presenter.prepareFailView(any()) }
        }

        @Test
        fun `should fail when systematic study does not exist`() {
            val request = FindAllReviewerStudiesBySearchService.RequestModel(
                userId = factory.researcherId,
                systematicStudyId = factory.systematicStudyId,
                reviewerId = factory.researcherId
            )

            preconditionCheckerMocking.makeSystematicStudyNonexistent()
            sut.findAllBySearch(presenter, request)

            verify(exactly = 1) { presenter.prepareFailView(any()) }
        }
    }
}