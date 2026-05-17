package br.all.application.review.create

import br.all.application.user.CredentialsService
import br.all.application.review.create.InviteCollaboratorService.RequestModel
import br.all.application.review.create.InviteCollaboratorService.ResponseModel
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.shared.presenter.prepareIfUnauthorized
import br.all.application.user.email.EmailBuilder
import br.all.application.user.email.EmailService
import br.all.application.user.repository.TokenStatus
import br.all.domain.shared.exception.AccountNotEnabledException
import br.all.domain.shared.exception.EntityNotFoundException
import br.all.domain.shared.exception.UnauthorizedUserException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class InviteCollaboratorServiceImpl(
    private val systematicStudyRepository: SystematicStudyRepository,
    private val credentialsService: CredentialsService,
    private val emailService: EmailService,
    private val emailBuilder: EmailBuilder,
    private val generateCollaboratorTokenService: GenerateCollaboratorTokenService
) : InviteCollaboratorService {
    
    @Transactional
    override fun create(presenter: InviteCollaboratorPresenter, request: RequestModel) {
        val userCredentials = credentialsService.loadCredentials(request.userId)
        val userCollaboratorCredentials = credentialsService.loadEnabledCredentialsByUsername(request.usernameCollaborator)
        val systematicStudy = systematicStudyRepository.findById(request.systematicStudyId)

        if (userCollaboratorCredentials == null) {
            presenter.prepareFailView(
                EntityNotFoundException("Collaborator user not found")
            )

            return
        }

        if (userCredentials == null) {
            presenter.prepareFailView(
                EntityNotFoundException("User not found")
            )

            return
        }

        if (systematicStudy == null) {
            presenter.prepareFailView(
                EntityNotFoundException("Systematic study not found")
            )

            return
        }

        if (systematicStudy.owner != userCredentials.id) {
            presenter.prepareFailView(
                UnauthorizedUserException("User doesnt have enough permissions to perform this action")
            )
        }

        userCollaboratorCredentials.isEnabled.let {
            if (!it) {
                presenter.prepareFailView(
                    AccountNotEnabledException("Collaborator user is not enabled")
                )
            }
        }

        val user = userCredentials.toUser()
        presenter.prepareIfUnauthorized(user)

        if (presenter.isDone()) return

        val token = generateCollaboratorTokenService.generateCollaboratorToken(systematicStudy.id, userCollaboratorCredentials.id)
        val emailMessage = emailBuilder.buildInviteCollaborator(
            email = userCollaboratorCredentials.email,
            name = userCollaboratorCredentials.name,
            token = token,
            inviter = userCredentials.username,
            title = systematicStudy.title,
            country = userCollaboratorCredentials.country
        )
        emailService.sendAsync(emailMessage)
        presenter.prepareSuccessView(ResponseModel(request.usernameCollaborator, userCollaboratorCredentials.email,
            TokenStatus.PENDENTE))
    }
}
