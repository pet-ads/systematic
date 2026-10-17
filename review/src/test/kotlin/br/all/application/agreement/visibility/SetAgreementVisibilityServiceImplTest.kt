package br.all.application.agreement

import br.all.application.agreement.repository.AgreementVisibilityDto
import br.all.application.agreement.repository.AgreementVisibilityRepository
import br.all.application.agreement.visibility.SetAgreementVisibilityPresenter
import br.all.application.agreement.visibility.SetAgreementVisibilityService.RequestModel
import br.all.application.agreement.visibility.SetAgreementVisibilityService.ResponseModel
import br.all.application.agreement.visibility.SetAgreementVisibilityServiceImpl
import br.all.application.review.repository.SystematicStudyDto
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.user.CredentialsService
import br.all.application.util.PreconditionCheckerMockingNew
import br.all.domain.model.agreement.AgreementVisibility
import br.all.domain.model.study.StudyReviewStage
import br.all.domain.shared.exception.UnauthorizedUserException
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.Runs
import io.mockk.verify
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import java.util.*

@Tag("UnitTest")
@Tag("ServiceTest")
@ExtendWith(MockKExtension::class)
class SetAgreementVisibilityServiceImplTest {

    @MockK
    private lateinit var systematicStudyRepository: SystematicStudyRepository
    @MockK
    private lateinit var agreementVisibilityRepository: AgreementVisibilityRepository
    @MockK
    private lateinit var credentialsService: CredentialsService
    @MockK(relaxed = true)
    private lateinit var presenter: SetAgreementVisibilityPresenter

    private lateinit var sut: SetAgreementVisibilityServiceImpl
    private lateinit var preconditionCheckerMocking: PreconditionCheckerMockingNew

    private val userId = UUID.randomUUID()
    private val systematicStudyId = UUID.randomUUID()

    private fun systematicStudyOwnedBy(ownerId: UUID) = SystematicStudyDto(
        id = systematicStudyId,
        title = "Title",
        description = "Description",
        owner = ownerId,
        collaborators = setOf(ownerId, userId),
        objectives = "Objectives",
    )

    @BeforeEach
    fun setUp() {
        preconditionCheckerMocking = PreconditionCheckerMockingNew(
            presenter, credentialsService, systematicStudyRepository, userId, systematicStudyId
        )
        sut = SetAgreementVisibilityServiceImpl(
            systematicStudyRepository, agreementVisibilityRepository, credentialsService
        )
    }

    @Nested
    @Tag("ValidClasses")
    @DisplayName("When successfully changing the agreement visibility")
    inner class WhenSuccessfullyChangingTheVisibility {
        @BeforeEach
        fun setUp() {
            preconditionCheckerMocking.makeEverythingWork()
            every { systematicStudyRepository.findById(systematicStudyId) } returns systematicStudyOwnedBy(userId)
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
        private val request = RequestModel(userId, systematicStudyId, StudyReviewStage.SELECTION, AgreementVisibility.NONE)

        @Test
        fun `should not allow a collaborator who is not the owner`() {
            preconditionCheckerMocking.makeEverythingWork()
            every { systematicStudyRepository.findById(systematicStudyId) } returns systematicStudyOwnedBy(UUID.randomUUID())

            sut.setVisibility(presenter, request)

            verify(exactly = 1) { presenter.prepareFailView(any<UnauthorizedUserException>()) }
            verify(exactly = 0) { agreementVisibilityRepository.saveOrUpdate(any()) }
        }

        @Test
        fun `should not be allowed when unauthenticated`() {
            preconditionCheckerMocking.testForUnauthenticatedUser(presenter, request) { _, _ ->
                sut.setVisibility(presenter, request)
            }
        }

        @Test
        fun `should not be allowed when unauthorized`() {
            preconditionCheckerMocking.testForUnauthorizedUser(presenter, request) { _, _ ->
                sut.setVisibility(presenter, request)
            }
        }

        @Test
        fun `should not be allowed when systematic study doesn't exist`() {
            preconditionCheckerMocking.testForNonexistentSystematicStudy(presenter, request) { _, _ ->
                sut.setVisibility(presenter, request)
            }
        }
    }
}