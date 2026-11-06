package br.all.application.study.find

import br.all.application.agreement.visibility.AgreementVisibilityPolicy
import br.all.application.reviewmode.repository.ReviewerStudyRepository
import br.all.application.shared.service.AuthorizationService
import br.all.application.study.find.presenter.FindAllStudyReviewsByReviewerPresenter
import br.all.application.study.find.service.FindAllStudyReviewsByReviewerService.ResponseModel
import br.all.application.study.find.service.FindAllStudyReviewsByReviewerServiceImpl
import br.all.application.study.util.TestDataFactory
import br.all.domain.model.agreement.AgreementVisibility
import br.all.domain.model.study.StudyReviewStage
import br.all.domain.shared.exception.UnauthorizedUserException
import br.all.domain.shared.user.Role
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
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
    private lateinit var authorizationService: AuthorizationService

    @MockK
    private lateinit var authorizationContext: AuthorizationService.AuthorizationContext

    @MockK
    private lateinit var reviewerStudyRepository: ReviewerStudyRepository

    @MockK
    private lateinit var agreementVisibilityPolicy: AgreementVisibilityPolicy

    @MockK(relaxed = true)
    private lateinit var presenter: FindAllStudyReviewsByReviewerPresenter

    @InjectMockKs
    private lateinit var sut: FindAllStudyReviewsByReviewerServiceImpl

    private val factory = TestDataFactory()
    private val userId = factory.researcherId
    private val systematicStudyId = factory.systematicStudyId
    private val otherReviewerId = UUID.randomUUID()

    private fun authorizedAs(role: Role) {
        every {
            authorizationService.authorize(presenter, userId, systematicStudyId, Role.entries.toSet())
        } returns authorizationContext
        every { authorizationContext.role } returns role
    }

    @Nested
    @Tag("ValidClasses")
    @DisplayName("When successfully finding study reviews by reviewer")
    inner class WhenSuccessfullyFindingStudyReviewsByReviewer {

        @Test
        fun `should find the user's own studies in the stage without consulting the visibility`() {
            authorizedAs(Role.REVIEWER)
            val request = factory.findAllByReviewerRequestModel(userId, StudyReviewStage.SELECTION)
            val reviewerStudies = List(3) { factory.generateReviewerStudyDto(it + 1L, userId) }
            every {
                reviewerStudyRepository.findAllByReviewerPaged(
                    systematicStudyId, userId, StudyReviewStage.SELECTION, any<Pageable>()
                )
            } returns PageImpl(reviewerStudies)

            sut.findAllByReviewer(presenter, request)

            val expected = ResponseModel(
                userId = userId,
                systematicStudyId = systematicStudyId,
                reviewerId = userId,
                stage = StudyReviewStage.SELECTION,
                studyReviews = reviewerStudies.map { it.studyReview },
                page = 0,
                size = 20,
                totalElements = 3,
                totalPages = 1
            )
            verify(exactly = 1) { presenter.prepareSuccessView(expected) }
            verify(exactly = 0) { agreementVisibilityPolicy.evaluate(any(), any(), any(), any()) }
        }

        @Test
        fun `should find the user's own studies of all stages when no stage is informed`() {
            authorizedAs(Role.REVIEWER)
            val request = factory.findAllByReviewerRequestModel(userId)
            every {
                reviewerStudyRepository.findAllByReviewerPaged(systematicStudyId, userId, null, any<Pageable>())
            } returns PageImpl(emptyList())

            sut.findAllByReviewer(presenter, request)

            verify(exactly = 1) { presenter.prepareSuccessView(match { it.stage == null && it.studyReviews.isEmpty() }) }
        }

        @Test
        fun `should find another reviewer's studies when the visibility allows`() {
            authorizedAs(Role.REVIEWER)
            val request = factory.findAllByReviewerRequestModel(otherReviewerId, StudyReviewStage.SELECTION)
            every {
                agreementVisibilityPolicy.evaluate(userId, systematicStudyId, StudyReviewStage.SELECTION, Role.REVIEWER)
            } returns AgreementVisibilityPolicy.Decision(AgreementVisibility.ALL, true)
            every {
                reviewerStudyRepository.findAllByReviewerPaged(
                    systematicStudyId, otherReviewerId, StudyReviewStage.SELECTION, any<Pageable>()
                )
            } returns PageImpl(listOf(factory.generateReviewerStudyDto(1L, otherReviewerId)))

            sut.findAllByReviewer(presenter, request)

            verify(exactly = 1) { presenter.prepareSuccessView(match { it.reviewerId == otherReviewerId }) }
        }

        @Test
        fun `should evaluate the visibility with the role of the user`() {
            authorizedAs(Role.OWNER)
            val request = factory.findAllByReviewerRequestModel(otherReviewerId, StudyReviewStage.EXTRACTION)
            every {
                agreementVisibilityPolicy.evaluate(userId, systematicStudyId, StudyReviewStage.EXTRACTION, Role.OWNER)
            } returns AgreementVisibilityPolicy.Decision(AgreementVisibility.NONE, true)
            every {
                reviewerStudyRepository.findAllByReviewerPaged(any(), any(), any(), any<Pageable>())
            } returns PageImpl(emptyList())

            sut.findAllByReviewer(presenter, request)

            verify(exactly = 1) {
                agreementVisibilityPolicy.evaluate(userId, systematicStudyId, StudyReviewStage.EXTRACTION, Role.OWNER)
            }
            verify(exactly = 1) { presenter.prepareSuccessView(any()) }
        }

        @Test
        fun `should forward page, size and sort to the repository`() {
            authorizedAs(Role.REVIEWER)
            val request = factory.findAllByReviewerRequestModel(userId, page = 2, pageSize = 5, sort = "year,desc")
            val pageable = slot<Pageable>()
            every {
                reviewerStudyRepository.findAllByReviewerPaged(systematicStudyId, userId, null, capture(pageable))
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
        fun `should not find another reviewer's studies when the visibility does not allow`() {
            authorizedAs(Role.REVIEWER)
            val request = factory.findAllByReviewerRequestModel(otherReviewerId, StudyReviewStage.SELECTION)
            every {
                agreementVisibilityPolicy.evaluate(userId, systematicStudyId, StudyReviewStage.SELECTION, Role.REVIEWER)
            } returns AgreementVisibilityPolicy.Decision(
                AgreementVisibility.NONE, false, AgreementVisibilityPolicy.Reason.HIDDEN
            )

            sut.findAllByReviewer(presenter, request)

            verify(exactly = 1) { presenter.prepareFailView(any<UnauthorizedUserException>()) }
            verify(exactly = 0) { reviewerStudyRepository.findAllByReviewerPaged(any(), any(), any(), any()) }
        }

        @Test
        fun `should require the stage to see another reviewer's studies`() {
            authorizedAs(Role.REVIEWER)
            val request = factory.findAllByReviewerRequestModel(otherReviewerId)

            sut.findAllByReviewer(presenter, request)

            verify(exactly = 1) { presenter.prepareFailView(any<IllegalArgumentException>()) }
            verify(exactly = 0) { agreementVisibilityPolicy.evaluate(any(), any(), any(), any()) }
            verify(exactly = 0) { reviewerStudyRepository.findAllByReviewerPaged(any(), any(), any(), any()) }
        }

        @Test
        fun `should not do anything when the user is not authorized`() {
            every {
                authorizationService.authorize(presenter, userId, systematicStudyId, Role.entries.toSet())
            } returns null

            sut.findAllByReviewer(presenter, factory.findAllByReviewerRequestModel(userId))

            verify(exactly = 0) { reviewerStudyRepository.findAllByReviewerPaged(any(), any(), any(), any()) }
            verify(exactly = 0) { presenter.prepareSuccessView(any()) }
        }
    }
}