package br.all.application.util

import br.all.application.researcher.credentials.ResearcherCredentialsService
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.shared.presenter.GenericPresenter
import br.all.domain.model.researcher.toResearcherId
import br.all.domain.model.review.toSystematicStudyId
import io.mockk.every
import java.util.*

class PreconditionCheckerMocking(
    private val presenter: GenericPresenter<*>,
    private val credentialsService: ResearcherCredentialsService,
    private val systematicStudyRepository: SystematicStudyRepository,
    researcher: UUID,
    systematicStudy: UUID,
) {
    private val researcher = researcher.toResearcherId()
    private val systematicStudy = systematicStudy.toSystematicStudyId()

    fun makeEverythingWork() {
        println(researcher.value())
        every { credentialsService.isAuthenticated(researcher) } returns true
        every { credentialsService.hasAuthority(researcher) } returns true
        every { systematicStudyRepository.existsById(systematicStudy.value()) } returns true
        every { systematicStudyRepository.hasReviewer(systematicStudy.value(), researcher.value()) } returns true
    }

    fun makeResearcherUnauthenticated() {
        every { credentialsService.isAuthenticated(researcher) } returns false
        every { credentialsService.hasAuthority(researcher) } returns true
        every { systematicStudyRepository.existsById(systematicStudy.value()) } returns true
        every { systematicStudyRepository.hasReviewer(systematicStudy.value(), researcher.value()) } returns true
        every { presenter.isDone() } returns true
    }

    fun makeResearcherUnauthorized() {
        every { credentialsService.isAuthenticated(researcher) } returns true
        every { credentialsService.hasAuthority(researcher) } returns false
        every { systematicStudyRepository.existsById(systematicStudy.value()) } returns true
        every { systematicStudyRepository.hasReviewer(systematicStudy.value(), researcher.value()) } returns true
        every { presenter.isDone() } returns true
    }

    fun makeSystematicStudyNonexistent() {
        every { credentialsService.isAuthenticated(researcher) } returns true
        every { credentialsService.hasAuthority(researcher) } returns true
        every { systematicStudyRepository.existsById(systematicStudy.value()) } returns false
        every { systematicStudyRepository.hasReviewer(systematicStudy.value(), researcher.value()) } returns false
        every { presenter.isDone() } returns false andThen true
    }

    fun makeResearcherNotACollaborator() {
        every { credentialsService.isAuthenticated(researcher) } returns true
        every { credentialsService.hasAuthority(researcher) } returns true
        every { systematicStudyRepository.existsById(systematicStudy.value()) } returns true
        every { systematicStudyRepository.hasReviewer(systematicStudy.value(), researcher.value()) } returns false
        every { presenter.isDone() } returns false andThen true
    }
}
