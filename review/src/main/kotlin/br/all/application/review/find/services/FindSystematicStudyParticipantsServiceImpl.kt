package br.all.application.review.find.services

import br.all.application.review.find.presenter.FindSystematicStudyParticipantsPresenter
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.shared.presenter.prepareIfUnauthorized
import br.all.application.user.CredentialsService
import java.util.*

class FindSystematicStudyParticipantsServiceImpl(
    private val repository: SystematicStudyRepository,
    private val credentialsService: CredentialsService,
) : FindSystematicStudyParticipantsService {

    override fun findParticipants(
        presenter: FindSystematicStudyParticipantsPresenter,
        request: FindSystematicStudyParticipantsService.RequestModel
    ) {

        val (userId, systematicStudyId) = request

        if (userNotAllowed(presenter, userId)) return

        val study = repository.findById(systematicStudyId)
            ?: throw NoSuchElementException(
                "Systematic study $systematicStudyId not found"
            )

        presenter.prepareSuccessView(
            FindSystematicStudyParticipantsService.ResponseModel(
                systematicStudyId = systematicStudyId,
                participants = study.collaborators.toSet()
            )
        )
    }

    private fun userNotAllowed(
        presenter: FindSystematicStudyParticipantsPresenter,
        userId: UUID,
    ): Boolean {

        val user = credentialsService.loadCredentials(userId)?.toUser()

        presenter.prepareIfUnauthorized(user)

        return presenter.isDone()
    }
}