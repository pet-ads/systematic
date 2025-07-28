package br.all.application.util

import br.all.application.collaboration.repository.CollaborationDto
import br.all.application.collaboration.repository.CollaborationRepository
import br.all.application.question.repository.QuestionRepository
import br.all.application.review.repository.SystematicStudyDto
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.shared.exceptions.EntityNotFoundException
import br.all.application.shared.exceptions.UnauthenticatedUserException
import br.all.application.shared.exceptions.UnauthorizedUserException
import br.all.application.shared.presenter.GenericPresenter
import br.all.application.shared.presenter.prepareIfFailsPreconditions
import br.all.application.user.CredentialsService
import io.github.serpro69.kfaker.Faker
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.verifyOrder
import java.util.*

class PreconditionCheckerMockingNew(
    private val presenter: GenericPresenter<*>,
    private val credentialsService: CredentialsService,
    private val systematicStudyRepository: SystematicStudyRepository,
    private val collaborationRepository: CollaborationRepository,
    private val userId: UUID,
    private val systematicStudyId: UUID,
    private val collaborationId: UUID,
) {
    private val faker = Faker()
    private val systematicStudy = generateSystematicStudy()

    fun makeEverythingWork() {
        val user = generateUserDto()
        mockkStatic(GenericPresenter<*>::prepareIfFailsPreconditions)
        every { credentialsService.loadCredentials(userId) } returns user
        every { systematicStudyRepository.findById(systematicStudyId) } returns systematicStudy
        every { presenter.prepareIfFailsPreconditions(
            any(),
            any(),
            allowedRoles = any(),
            collaborations = any()
        ) } returns Unit
        every { collaborationRepository.
                listAllCollaborationsBySystematicStudyId(systematicStudyId)
        } returns collaborationId
        every { presenter.isDone() } returns false
    }

    fun makeUserUnauthenticated() {
        every { credentialsService.loadCredentials(userId) } returns null
        every { systematicStudyRepository.findById(systematicStudyId) } returns systematicStudy
        every { presenter.isDone() } returns true
    }

    fun makeUserUnauthorized() {
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

    fun makeQuestionNonexistent(questionId: UUID, repository: QuestionRepository) {
        val user = generateUserDto()
        every { credentialsService.loadCredentials(userId) } returns user
        every { systematicStudyRepository.findById(systematicStudyId) } returns systematicStudy
        every { repository.findById(systematicStudyId, questionId) } returns null
        every { presenter.isDone() } returns false andThen false andThen true
    }

    fun <T>testForUnauthorizedUser(
        presenter: GenericPresenter<*>,
        request: T,
        service: (presenter: GenericPresenter<*>, requestModel: T) -> Unit
    ){
        this.makeUserUnauthorized()
        service(presenter, request)
        verifyOrder {
            presenter.prepareFailView(ofType(UnauthorizedUserException::class))
            presenter.isDone()
        }
    }

    fun <T>testForUnauthenticatedUser(
        presenter: GenericPresenter<*>,
        request: T,
        service: (presenter: GenericPresenter<*>, requestModel: T) -> Unit
    ){
        this.makeUserUnauthenticated()
        service(presenter, request)
        verifyOrder {
            presenter.prepareFailView(ofType(UnauthenticatedUserException::class))
            presenter.isDone()
        }
    }

    fun <T>testForNonexistentSystematicStudy(
        presenter: GenericPresenter<*>,
        request: T,
        service: (presenter: GenericPresenter<*>, requestModel: T) -> Unit
    ){
        this.makeSystematicStudyNonexistent()
        service(presenter, request)
        verifyOrder {
            presenter.isDone()
            presenter.prepareFailView(ofType(EntityNotFoundException::class))
        }
    }

    fun <T>testForNonexistentQuestion(
        presenter: GenericPresenter<*>,
        request: T,
        questionId: UUID,
        questionRepository: QuestionRepository,
        service: (presenter: GenericPresenter<*>, requestModel: T) -> Unit
    ){
        this.makeQuestionNonexistent(questionId, questionRepository)
        service(presenter, request)
        verifyOrder {
            presenter.isDone()
            presenter.prepareFailView(ofType(EntityNotFoundException::class))
        }
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

    private fun generateCollaborationDto(
        id: UUID = collaborationId,
        status: String = faker.rockBand.name(),
        permissions: Set<String> = emptySet()
    ) = CollaborationDto(
        id = id,
        systematicStudyId = systematicStudyId,
        userId = userId,
        status = status,
        permissions = permissions
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
