package br.all.application.shared.presenter

import br.all.application.researcher.credentials.ResearcherCredentialsService
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.shared.exceptions.EntityNotFoundException
import br.all.application.shared.exceptions.UnauthenticatedUserException
import br.all.application.shared.exceptions.UnauthorizedUserException
import br.all.domain.model.researcher.ResearcherId
import br.all.domain.model.review.SystematicStudyId
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class PreconditionCheckerTest {

    private lateinit var presenter: GenericPresenter<*>
    private lateinit var credentialsService: ResearcherCredentialsService
    private lateinit var repository: SystematicStudyRepository
    private lateinit var preconditionChecker: PreconditionChecker

    @BeforeEach
    fun setUp() {
        presenter = mockk(relaxed = true)
        credentialsService = mockk()
        repository = mockk(relaxed = true)
        preconditionChecker = PreconditionChecker(repository, credentialsService)
    }

    @Test
    fun `should not invoke prepareFailView when all preconditions are met`() {
        val researcherId = ResearcherId(UUID.randomUUID())
        val systematicStudyId = SystematicStudyId(UUID.randomUUID())
        every { credentialsService.isAuthenticated(researcherId) } returns true
        every { credentialsService.hasAuthority(researcherId) } returns true
        every { repository.existsById(systematicStudyId.value()) } returns true
        every { repository.hasReviewer(systematicStudyId.value(), researcherId.value) } returns true

        preconditionChecker.prepareIfViolatesPreconditions(presenter, researcherId, systematicStudyId)

        verify(exactly = 0) { presenter.prepareFailView(any()) }
    }

    @Test
    fun `should invoke prepareFailView with UnauthenticatedUserException when not authenticated`() {
        val researcherId = ResearcherId(UUID.randomUUID())
        val systematicStudyId = SystematicStudyId(UUID.randomUUID())
        every { credentialsService.isAuthenticated(researcherId)} returns false

        preconditionChecker.prepareIfViolatesPreconditions(presenter, researcherId, systematicStudyId)

        verify { presenter.prepareFailView(match { it is UnauthenticatedUserException }) }
    }

    @Test
    fun `should invoke prepareFailView with UnauthenticatedUserException when not authorized`() {
        val researcherId = ResearcherId(UUID.randomUUID())
        val systematicStudyId = SystematicStudyId(UUID.randomUUID())
        every { credentialsService.isAuthenticated(researcherId) } returns true
        every { credentialsService.hasAuthority(researcherId) } returns false

        preconditionChecker.prepareIfViolatesPreconditions(presenter, researcherId, systematicStudyId)

        verify { presenter.prepareFailView(match { it is UnauthorizedUserException }) }
    }

    @Test
    fun `should invoke prepareFailView with EntityNotFoundException when review does not exist`() {
        val researcherId = ResearcherId(UUID.randomUUID())
        val systematicStudyId = SystematicStudyId(UUID.randomUUID())
        every { credentialsService.isAuthenticated(researcherId) } returns true
        every { credentialsService.hasAuthority(researcherId) } returns true
        every { repository.existsById(systematicStudyId.value()) } returns false

        preconditionChecker.prepareIfViolatesPreconditions(presenter, researcherId, systematicStudyId)

        verify { presenter.prepareFailView(match { it is EntityNotFoundException }) }
    }

    @Test
    fun `should invoke prepareFailView with UnauthorizedUserException when user is not a reviewer`() {
        val researcherId = ResearcherId(UUID.randomUUID())
        val systematicStudyId = SystematicStudyId(UUID.randomUUID())
        every { credentialsService.isAuthenticated(researcherId) } returns true
        every { credentialsService.hasAuthority(researcherId) } returns true
        every { repository.existsById(systematicStudyId.value()) } returns true
        every { repository.hasReviewer(systematicStudyId.value(), researcherId.value) } returns false

        preconditionChecker.prepareIfViolatesPreconditions(presenter, researcherId, systematicStudyId)

        verify { presenter.prepareFailView(match { it is UnauthorizedUserException }) }
    }
}
