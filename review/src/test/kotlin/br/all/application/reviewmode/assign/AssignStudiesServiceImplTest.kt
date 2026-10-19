package br.all.application.reviewmode.assign

import br.all.application.reviewmode.repository.ReviewModeRepository
import br.all.application.shared.service.AuthorizationService
import br.all.domain.model.study.StudyReviewStage
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
    private lateinit var authorizationService: AuthorizationService
    @MockK(relaxed = true)
    private lateinit var presenter: AssignStudiesPresenter

    @InjectMockKs
    private lateinit var sut: AssignStudiesServiceImpl

    private val researcherId = UUID.randomUUID()
    private val systematicStudyId = UUID.randomUUID()

    private val request = AssignStudiesService.RequestModel(
        userId = researcherId,
        systematicStudyId = systematicStudyId,
        stage = StudyReviewStage.SELECTION,
    )

    @Nested
    @Tag("InvalidClasses")
    @DisplayName("When preconditions fail")
    inner class WhenPreconditionsFail {
        @Test
        fun `should not look for the review mode when authorization fails`() {
            every {
                authorizationService.authorize(presenter, researcherId, systematicStudyId, setOf(Role.OWNER))
            } returns null

            sut.assign(presenter, request)

            verify(exactly = 0) { reviewModeRepository.findById(any()) }
        }
    }
}