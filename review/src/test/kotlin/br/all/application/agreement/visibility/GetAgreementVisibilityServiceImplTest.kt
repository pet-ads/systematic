package br.all.application.agreement

import br.all.application.agreement.repository.AgreementVisibilityDto
import br.all.application.agreement.repository.AgreementVisibilityRepository
import br.all.application.agreement.visibility.GetAgreementVisibilityPresenter
import br.all.application.agreement.visibility.GetAgreementVisibilityService.RequestModel
import br.all.application.agreement.visibility.GetAgreementVisibilityService.ResponseModel
import br.all.application.agreement.visibility.GetAgreementVisibilityServiceImpl
import br.all.application.review.repository.SystematicStudyDto
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.user.CredentialsService
import br.all.application.util.PreconditionCheckerMockingNew
import br.all.domain.model.agreement.AgreementVisibility
import io.mockk.every
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
    private lateinit var systematicStudyRepository: SystematicStudyRepository

    @MockK
    private lateinit var agreementVisibilityRepository: AgreementVisibilityRepository

    @MockK
    private lateinit var credentialsService: CredentialsService

    @MockK(relaxed = true)
    private lateinit var presenter: GetAgreementVisibilityPresenter

    private lateinit var sut: GetAgreementVisibilityServiceImpl
    private lateinit var preconditionCheckerMocking: PreconditionCheckerMockingNew

    private val userId = UUID.randomUUID()
    private val systematicStudyId = UUID.randomUUID()
    private val request = RequestModel(userId, systematicStudyId)

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
            presenter,
            credentialsService,
            systematicStudyRepository,
            userId,
            systematicStudyId
        )
        sut = GetAgreementVisibilityServiceImpl(
            systematicStudyRepository,
            agreementVisibilityRepository,
            credentialsService,
        )
    }

    @Nested
    @Tag("ValidClasses")
    @DisplayName("When successfully getting the agreement visibility")
    inner class WhenSuccessfullyGettingTheVisibility {
        @BeforeEach
        fun setUp() = run { preconditionCheckerMocking.makeEverythingWork() }

        @Test
        fun `should return the default visibility for both stages when none was configured`() {
            every { agreementVisibilityRepository.findById(systematicStudyId) } returns null

            sut.getVisibility(presenter, request)

            val expected = ResponseModel(
                userId,
                systematicStudyId,
                selection = AgreementVisibility.DEFAULT,
                extraction = AgreementVisibility.DEFAULT,
            )
            verify(exactly = 1) { presenter.prepareSuccessView(expected) }
            verify(exactly = 0) { presenter.prepareFailView(any()) }
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
                userId,
                systematicStudyId,
                selection = AgreementVisibility.ALL,
                extraction = AgreementVisibility.NONE,
            )
            verify(exactly = 1) { presenter.prepareSuccessView(expected) }
        }

        @Test
        fun `should not require the user to be the owner of the systematic study`() {
            every { systematicStudyRepository.findById(systematicStudyId) } returns
                    systematicStudyOwnedBy(UUID.randomUUID())
            every { agreementVisibilityRepository.findById(systematicStudyId) } returns AgreementVisibilityDto(
                systematicStudyId,
                selection = AgreementVisibility.FINISHED_ONLY,
                extraction = AgreementVisibility.ALL,
            )

            sut.getVisibility(presenter, request)

            val expected = ResponseModel(
                userId,
                systematicStudyId,
                selection = AgreementVisibility.FINISHED_ONLY,
                extraction = AgreementVisibility.ALL,
            )
            verify(exactly = 1) { presenter.prepareSuccessView(expected) }
        }

        @Test
        fun `should not write anything when reading the visibility`() {
            every { agreementVisibilityRepository.findById(systematicStudyId) } returns null

            sut.getVisibility(presenter, request)

            verify(exactly = 0) { agreementVisibilityRepository.saveOrUpdate(any()) }
        }
    }

    @Nested
    @Tag("InvalidClasses")
    @DisplayName("When failing to get the agreement visibility")
    inner class WhenFailingToGetTheVisibility {
        @Test
        fun `should not be allowed to get the visibility when unauthenticated`() {
            preconditionCheckerMocking.testForUnauthenticatedUser(presenter, request) { _, _ ->
                sut.getVisibility(presenter, request)
            }
            verify(exactly = 0) { agreementVisibilityRepository.findById(any()) }
        }

        @Test
        fun `should not be allowed to get the visibility when unauthorized`() {
            preconditionCheckerMocking.testForUnauthorizedUser(presenter, request) { _, _ ->
                sut.getVisibility(presenter, request)
            }
            verify(exactly = 0) { agreementVisibilityRepository.findById(any()) }
        }

        @Test
        fun `should not be allowed to get the visibility when systematic study doesn't exist`() {
            preconditionCheckerMocking.testForNonexistentSystematicStudy(presenter, request) { _, _ ->
                sut.getVisibility(presenter, request)
            }
            verify(exactly = 0) { agreementVisibilityRepository.findById(any()) }
        }
    }
}