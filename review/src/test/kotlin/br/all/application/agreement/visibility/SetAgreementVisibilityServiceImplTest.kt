package br.all.application.agreement.visibility

import br.all.application.agreement.repository.AgreementVisibilityDto
import br.all.application.agreement.repository.AgreementVisibilityRepository
import br.all.application.agreement.visibility.SetAgreementVisibilityService.RequestModel
import br.all.application.agreement.visibility.SetAgreementVisibilityService.ResponseModel
import br.all.application.shared.service.AuthorizationService
import br.all.domain.model.agreement.AgreementVisibility
import br.all.domain.model.study.StudyReviewStage
import br.all.domain.shared.user.Role
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.*

@Tag("UnitTest")
@Tag("ServiceTest")
@ExtendWith(MockKExtension::class)
class SetAgreementVisibilityServiceImplTest {

    @MockK
    private lateinit var authorizationService: AuthorizationService

    @MockK
    private lateinit var authorizationContext: AuthorizationService.AuthorizationContext

    @MockK
    private lateinit var agreementVisibilityRepository: AgreementVisibilityRepository

    @MockK(relaxed = true)
    private lateinit var presenter: SetAgreementVisibilityPresenter

    @InjectMockKs
    private lateinit var sut: SetAgreementVisibilityServiceImpl

    private val userId = UUID.randomUUID()
    private val systematicStudyId = UUID.randomUUID()

    @Nested
    @Tag("ValidClasses")
    @DisplayName("When successfully changing the agreement visibility")
    inner class WhenSuccessfullyChangingTheVisibility {
        @BeforeEach
        fun setUp() {
            every {
                authorizationService.authorize(presenter, userId, systematicStudyId, setOf(Role.OWNER))
            } returns authorizationContext
            every { agreementVisibilityRepository.saveOrUpdate(any()) } just Runs
        }

        @Test
        fun `should create the configuration with the default for the other stage when none exists`() {
            every { agreementVisibilityRepository.findById(systematicStudyId) } returns null

            sut.setVisibility(
                presenter, RequestModel(userId, systematicStudyId, StudyReviewStage.SELECTION, AgreementVisibility.ALL)
            )

            val expected = AgreementVisibilityDto(
                systematicStudyId, selection = AgreementVisibility.ALL, extraction = AgreementVisibility.DEFAULT
            )
            verify(exactly = 1) { agreementVisibilityRepository.saveOrUpdate(expected) }
            verify(exactly = 1) {
                presenter.prepareSuccessView(
                    ResponseModel(userId, systematicStudyId, AgreementVisibility.ALL, AgreementVisibility.DEFAULT)
                )
            }
        }

        @Test
        fun `should change only the informed stage and keep the other one`() {
            every { agreementVisibilityRepository.findById(systematicStudyId) } returns AgreementVisibilityDto(
                systematicStudyId, selection = AgreementVisibility.ALL, extraction = AgreementVisibility.NONE
            )

            sut.setVisibility(
                presenter,
                RequestModel(userId, systematicStudyId, StudyReviewStage.EXTRACTION, AgreementVisibility.FINISHED_ONLY)
            )

            val expected = AgreementVisibilityDto(
                systematicStudyId, selection = AgreementVisibility.ALL, extraction = AgreementVisibility.FINISHED_ONLY
            )
            verify(exactly = 1) { agreementVisibilityRepository.saveOrUpdate(expected) }
        }
    }

    @Nested
    @Tag("InvalidClasses")
    @DisplayName("When failing to change the agreement visibility")
    inner class WhenFailingToChangeTheVisibility {
        @Test
        fun `should not change anything when the user is not authorized`() {
            every {
                authorizationService.authorize(presenter, userId, systematicStudyId, setOf(Role.OWNER))
            } returns null

            sut.setVisibility(
                presenter, RequestModel(userId, systematicStudyId, StudyReviewStage.SELECTION, AgreementVisibility.NONE)
            )

            verify(exactly = 0) { agreementVisibilityRepository.findById(any()) }
            verify(exactly = 0) { agreementVisibilityRepository.saveOrUpdate(any()) }
            verify(exactly = 0) { presenter.prepareSuccessView(any<ResponseModel>()) }
        }
    }
}