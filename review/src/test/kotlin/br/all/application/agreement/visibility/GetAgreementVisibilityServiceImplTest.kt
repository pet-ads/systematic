package br.all.application.agreement.visibility

import br.all.application.agreement.repository.AgreementVisibilityDto
import br.all.application.agreement.repository.AgreementVisibilityRepository
import br.all.application.agreement.visibility.GetAgreementVisibilityService.RequestModel
import br.all.application.agreement.visibility.GetAgreementVisibilityService.ResponseModel
import br.all.application.shared.service.AuthorizationService
import br.all.domain.model.agreement.AgreementVisibility
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
import java.util.*

@Tag("UnitTest")
@Tag("ServiceTest")
@ExtendWith(MockKExtension::class)
class GetAgreementVisibilityServiceImplTest {

    @MockK
    private lateinit var authorizationService: AuthorizationService

    @MockK
    private lateinit var authorizationContext: AuthorizationService.AuthorizationContext

    @MockK
    private lateinit var agreementVisibilityRepository: AgreementVisibilityRepository

    @MockK(relaxed = true)
    private lateinit var presenter: GetAgreementVisibilityPresenter

    @InjectMockKs
    private lateinit var sut: GetAgreementVisibilityServiceImpl

    private val userId = UUID.randomUUID()
    private val systematicStudyId = UUID.randomUUID()
    private val request = RequestModel(userId, systematicStudyId)

    @Nested
    @Tag("ValidClasses")
    @DisplayName("When successfully getting the agreement visibility")
    inner class WhenSuccessfullyGettingTheVisibility {
        @BeforeEach
        fun setUp() {
            every {
                authorizationService.authorize(presenter, userId, systematicStudyId, Role.entries.toSet())
            } returns authorizationContext
        }

        @Test
        fun `should return the default visibility for both stages when none was configured`() {
            every { agreementVisibilityRepository.findById(systematicStudyId) } returns null

            sut.getVisibility(presenter, request)

            val expected = ResponseModel(
                userId, systematicStudyId,
                selection = AgreementVisibility.DEFAULT,
                extraction = AgreementVisibility.DEFAULT,
            )
            verify(exactly = 1) { presenter.prepareSuccessView(expected) }
        }

        @Test
        fun `should return the saved visibility of each stage`() {
            every { agreementVisibilityRepository.findById(systematicStudyId) } returns AgreementVisibilityDto(
                systematicStudyId,
                selection = AgreementVisibility.ALL,
                extraction = AgreementVisibility.NONE,
            )

            sut.getVisibility(presenter, request)

            val expected = ResponseModel(
                userId, systematicStudyId,
                selection = AgreementVisibility.ALL,
                extraction = AgreementVisibility.NONE,
            )
            verify(exactly = 1) { presenter.prepareSuccessView(expected) }
        }
    }

    @Nested
    @Tag("InvalidClasses")
    @DisplayName("When failing to get the agreement visibility")
    inner class WhenFailingToGetTheVisibility {
        @Test
        fun `should not read the visibility when the user is not authorized`() {
            every {
                authorizationService.authorize(presenter, userId, systematicStudyId, Role.entries.toSet())
            } returns null

            sut.getVisibility(presenter, request)

            verify(exactly = 0) { agreementVisibilityRepository.findById(any()) }
            verify(exactly = 0) { presenter.prepareSuccessView(any<ResponseModel>()) }
        }
    }
}