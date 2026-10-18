package br.all.application.reviewmode.find

import br.all.application.reviewmode.repository.ReviewModeDto
import br.all.application.reviewmode.repository.ReviewModeRepository
import br.all.application.reviewmode.repository.toDto
import br.all.application.shared.service.AuthorizationService
import br.all.domain.model.reviewmode.Split
import br.all.domain.shared.user.ResearcherId
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.extension.ExtendWith
import java.util.UUID
import kotlin.test.Test

@Tag("ServiceTest")
@Tag("UnitTest")
@ExtendWith(MockKExtension::class)
class FindReviewModeServiceImplTest {
    @MockK
    private lateinit var authorizationService: AuthorizationService
    @MockK
    private lateinit var reviewModeRepository: ReviewModeRepository
    @MockK(relaxed = true)
    private lateinit var presenter: FindReviewModePresenter
    @InjectMockKs
    private lateinit var sut: FindReviewModeServiceImpl

    private val researcherId = UUID.randomUUID()
    private val systematicStudyId = UUID.randomUUID()
    private val request = FindReviewModeService.RequestModel(researcherId, systematicStudyId)

    @Nested
    @Tag("ValidClasses")
    @DisplayName("When getting a review mode")
    inner class WhenGettingAReviewMode {
        @BeforeEach
        fun setUp() {
            every { authorizationService.authorize(presenter, researcherId, systematicStudyId, any()) } returns
                    mockk<AuthorizationService.AuthorizationContext>()
            every { reviewModeRepository.findById(systematicStudyId) } returns
                    ReviewModeDto(systematicStudyId, selection = Split(listOf(ResearcherId(researcherId)), 1).  toDto())
        }

        @Test
        fun `should prepare success view with the review mode of the systematic study`() {
            sut.find(presenter, request)

            verify(exactly = 1) {
                presenter.prepareSuccessView(match<FindReviewModeService.ResponseModel> {
                    it.systematicStudyId == systematicStudyId &&
                            it.selection is Split &&
                            it.extraction == null
                })
            }
        }
    }

    @Nested
    @Tag("InvalidClasses")
    @DisplayName("When researcher is not authorized")
    inner class WhenResearcherIsNotAuthorized {
        @Test
        fun `should not look for the review mode`() {
            every { authorizationService.authorize(presenter, researcherId, systematicStudyId, any()) } returns null

            sut.find(presenter, request)

            verify(exactly = 0) { reviewModeRepository.findById(any()) }
        }
    }
}