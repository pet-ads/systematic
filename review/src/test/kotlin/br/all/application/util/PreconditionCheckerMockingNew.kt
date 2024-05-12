package br.all.application.util

import br.all.application.question.repository.QuestionRepository
import br.all.application.user.credentials.ResearcherCredentialsService
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.shared.presenter.GenericPresenter
import br.all.application.shared.presenter.prepareIfFailsPreconditions
import br.all.application.shared.presenter.prepareIfUnauthorized
import br.all.application.user.CredentialsService
import br.all.domain.model.researcher.toResearcherId
import br.all.domain.model.review.toSystematicStudyId
import io.mockk.every
import io.mockk.just
import io.mockk.mockkStatic
import io.mockk.runs
import java.util.*

class PreconditionCheckerMockingNew(
    private val presenter: GenericPresenter<*>,
    private val credentialsService: CredentialsService,
    researcher: UUID,
    systematicStudy: UUID,
) {
    private val researcher = researcher.toResearcherId()
    private val systematicStudy = systematicStudy.toSystematicStudyId()

    fun makeEverythingWork() {
        mockkStatic(GenericPresenter<*>::prepareIfFailsPreconditions)
        every { presenter.prepareIfFailsPreconditions(any(), any()) } returns Unit
        every { presenter.isDone() } returns false
    }

    fun makeResearcherUnauthenticated() {
        every { presenter.isDone() } returns true
    }

    fun makeResearcherUnauthorized() {
        every { presenter.isDone() } returns true
    }

    fun mockPresenterIsDone(){
        every { presenter.isDone() } returns true
    }

    fun makeSystematicStudyNonexistent() {
        every { presenter.isDone() } returns false andThen true
    }

    fun makeQuestionNonexistent(questionRepository: QuestionRepository, questionId: UUID) {
        every { presenter.isDone() } returns false
    }

    fun makeResearcherNotACollaborator() {
        every { presenter.isDone() } returns false andThen true
    }
}
