package br.all.application.util

import br.all.application.question.repository.QuestionRepository
import br.all.application.review.repository.SystematicStudyDto
import br.all.application.review.repository.SystematicStudyRepository
import br.all.domain.shared.exception.EntityNotFoundException
import br.all.domain.shared.exception.UnauthenticatedUserException
import br.all.domain.shared.exception.UnauthorizedUserException
import br.all.domain.shared.presenter.GenericPresenter
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
    private val userId: UUID,
    private val systematicStudyId: UUID,
) {
    private val faker = Faker()
    private val systematicStudy = generateSystematicStudy()

    fun makeEverythingWork() {
        val user = generateUserDto()
        val userEnabled = generateUserEnabledCredentialsDto()
        mockkStatic(GenericPresenter<*>::prepareIfFailsPreconditions)
        every { credentialsService.loadCredentials(userId) } returns user
        every { credentialsService.loadEnabledCredentialsById(userId) } returns userEnabled
        every { systematicStudyRepository.findById(systematicStudyId) } returns systematicStudy
        every { presenter.prepareIfFailsPreconditions(any(), any()) } returns Unit
        every { presenter.isDone() } returns false
    }

    fun makeUserUnauthenticated() {
        every { credentialsService.loadEnabledCredentialsById(userId) } returns null
        every { credentialsService.loadCredentials(userId) } returns null
        every { systematicStudyRepository.findById(systematicStudyId) } returns systematicStudy
        every { presenter.isDone() } returns true
    }

    fun makeUserUnauthorized() {
        val user = generateUnauthorizedUserDto()
        val userEnabled = generateUserEnabledCredentialsDto()
        every { credentialsService.loadCredentials(userId) } returns user
        every { credentialsService.loadEnabledCredentialsById(userId)  } returns userEnabled
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
        objectives: String = faker.beer.brand()
    ) = SystematicStudyDto(
        id,
        title,
        description,
        ownerId,
        mutableSetOf(ownerId).also { it.addAll(collaborators) },
        objectives
    )

    private fun generateUserEnabledCredentialsDto(
        userId: UUID = this.userId,
        userName: String = faker.name.firstName(),
        userCountry: String = faker.address.country(),
        isEnabled: Boolean = true,
        email: String = faker.internet.email(),
    ) = CredentialsService.InformationResponseModel(userId, userName, userCountry, isEnabled, email)


    private fun generateUserDto(
        userId: UUID = this.userId,
        userName: String = faker.name.firstName(),
        userRoles: Set<String> = setOf("USER"),
    ) = CredentialsService.ResponseModel(userId, userName, userRoles)

    private fun generateUnauthorizedUserDto(
        userId: UUID = this.userId,
        userName: String = faker.name.firstName(),
        userRoles: Set<String> = emptySet()
    ) = CredentialsService.ResponseModel(userId, userName, userRoles)
}
