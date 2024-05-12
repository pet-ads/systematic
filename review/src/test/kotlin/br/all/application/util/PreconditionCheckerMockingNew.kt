package br.all.application.util

import br.all.application.question.repository.QuestionRepository
import br.all.application.review.repository.SystematicStudyDto
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.shared.presenter.GenericPresenter
import br.all.application.shared.presenter.prepareIfFailsPreconditions
import br.all.application.user.CredentialsService
import io.github.serpro69.kfaker.Faker
import io.mockk.every
import io.mockk.mockkStatic
import java.util.*

class PreconditionCheckerMockingNew(
    private val presenter: GenericPresenter<*>,
    private val credentialsService: CredentialsService,
    private val systematicStudyRepository: SystematicStudyRepository,
    private val userId: UUID,
    private val systematicStudyId: UUID,
) {
    private val faker = Faker()
    private val systematicStudy = generateSystematicStudy()

    fun makeEverythingWork() {
        val user = generateUserDto()
        mockkStatic(GenericPresenter<*>::prepareIfFailsPreconditions)
        every { credentialsService.loadCredentials(userId) } returns user
        every { systematicStudyRepository.findById(systematicStudyId) } returns systematicStudy
        every { presenter.prepareIfFailsPreconditions(any(), any()) } returns Unit
        every { presenter.isDone() } returns false
    }

    fun makeResearcherUnauthenticated() {
        every { credentialsService.loadCredentials(userId) } returns null
        every { systematicStudyRepository.findById(systematicStudyId) } returns systematicStudy
        every { presenter.isDone() } returns true
    }

    fun makeResearcherUnauthorized() {
        val user = generateUnauthorizedUserDto()
        every { credentialsService.loadCredentials(userId) } returns user
        every { systematicStudyRepository.findById(systematicStudyId) } returns systematicStudy
        every { presenter.isDone() } returns true
    }

    fun makeSystematicStudyNonexistent() {
        val user = generateUserDto()
        every { credentialsService.loadCredentials(userId) } returns user
        every { systematicStudyRepository.findById(systematicStudyId) } returns null
        every { presenter.isDone() } returns false andThen true
    }

    fun makeQuestionNonexistent(questionRepository: QuestionRepository, questionId: UUID) {
        every { presenter.isDone() } returns false
    }

    fun makeResearcherNotACollaborator() {
        every { presenter.isDone() } returns false andThen true
    }

    private fun generateSystematicStudy(
        id: UUID = systematicStudyId,
        title: String = faker.book.title(),
        description: String = faker.lorem.words(),
        ownerId: UUID = userId,
        collaborators: Set<UUID> = emptySet(),
    ) = SystematicStudyDto(
        id,
        title,
        description,
        ownerId,
        mutableSetOf(ownerId).also { it.addAll(collaborators) },
    )

    private fun generateUserDto(
        userId: UUID = this.userId,
        userName: String = faker.name.firstName(),
        userRoles: Set<String> = setOf("COLLABORATOR")
    ) = CredentialsService.ResponseModel(userId, userName, userRoles)

    private fun generateUnauthorizedUserDto(
        userId: UUID = this.userId,
        userName: String = faker.name.firstName(),
        userRoles: Set<String> = emptySet()
    ) = CredentialsService.ResponseModel(userId, userName, userRoles)

}
