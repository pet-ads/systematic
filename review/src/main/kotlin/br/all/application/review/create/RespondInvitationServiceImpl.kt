package br.all.application.review.create

import br.all.application.review.create.RespondInvitationService.RequestModel
import br.all.application.review.repository.CollaboratorTokenRepository
import br.all.application.review.repository.InviteResponse
import br.all.application.user.repository.TokenStatus
import br.all.domain.shared.user.Role
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class RespondInvitationServiceImpl(
    private val tokenRepository: CollaboratorTokenRepository,
    private val addCollaboratorService: AddCollaboratorService
) : RespondInvitationService {
    @Transactional
    override fun create(presenter: ResponseInvitationPresenter, request: RequestModel) {
        val token = tokenRepository.findById(request.token)
            ?: return presenter.prepareFailView(IllegalArgumentException("Invalid token"))

        if (token.status == TokenStatus.CONCLUIDO) {
            presenter.prepareFailView(IllegalArgumentException("Token already used"))
            return
        }

        if (token.expiration.isBefore(LocalDateTime.now())) {
            presenter.prepareFailView(IllegalArgumentException("Expired token"))
            return
        }

        if (request.inviteResponse == InviteResponse.REJECTED) {
            token.status = TokenStatus.REJEITADO
            token.expiration = LocalDateTime.now().plusDays(7)
            tokenRepository.saveOrUpdate(token)

            return presenter.prepareSuccessView(RespondInvitationService.ResponseModel())
        }

        try {
            addCollaboratorService.addCollaborator(token.researcherId, token.systematicStudyId, Role.COLLABORATOR)

            token.status = TokenStatus.CONCLUIDO
            token.expiration = LocalDateTime.now().plusDays(1)
            tokenRepository.saveOrUpdate(token)

            return presenter.prepareSuccessView(RespondInvitationService.ResponseModel())
        } catch (e: Exception) {
            presenter.prepareFailView(e)
        }

    }
}
