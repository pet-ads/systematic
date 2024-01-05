package br.all.application.review.util

import br.all.application.researcher.credentials.ResearcherCredentialsService
import br.all.application.shared.exceptions.UnauthenticatedUserException
import br.all.application.shared.exceptions.UnauthorizedUserException
import br.all.application.shared.presenter.GenericPresenter
import br.all.domain.model.researcher.ResearcherId
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import java.util.*

object CredentialsServiceMockBuilder {
    fun makeResearcherToBeAllowed(
        credentialsService: ResearcherCredentialsService,
        presenter: GenericPresenter<*>,
        researcherUuid: UUID,
    ) {
        val researcherId = ResearcherId(researcherUuid)
        every { credentialsService.isAuthenticated(researcherId) } returns true
        every { credentialsService.hasAuthority(researcherId) } returns true
        every { presenter.isDone() } returns false
    }

    fun makeResearcherToBeUnauthenticated(
        credentialsService: ResearcherCredentialsService,
        presenter: GenericPresenter<*>,
        researcherUuid: UUID,
    ) {
        val researcherId = ResearcherId(researcherUuid)
        every { credentialsService.isAuthenticated(researcherId) } returns false
        every { credentialsService.hasAuthority(researcherId) } returns true
        every { presenter.prepareFailView(any<UnauthenticatedUserException>()) } just Runs
        every { presenter.isDone() } returns true
    }

    fun makeResearcherToBeUnauthorized(
        credentialsService: ResearcherCredentialsService,
        presenter: GenericPresenter<*>,
        researcherUuid: UUID,
    ) {
        val researcherId = ResearcherId(researcherUuid)
        every { credentialsService.isAuthenticated(researcherId) } returns true
        every { credentialsService.hasAuthority(researcherId) } returns false
        every { presenter.prepareFailView(any<UnauthorizedUserException>()) } just Runs
        every { presenter.isDone() } returns true
    }
}