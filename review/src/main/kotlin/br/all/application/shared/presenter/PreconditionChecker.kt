package br.all.application.shared.presenter

import br.all.application.protocol.repository.ProtocolRepository
import br.all.application.researcher.credentials.ResearcherCredentialsService
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.shared.exceptions.EntityNotFoundException
import br.all.application.shared.exceptions.UnauthenticatedUserException
import br.all.application.shared.exceptions.UnauthorizedUserException
import br.all.domain.model.protocol.ProtocolId
import br.all.domain.model.researcher.ResearcherId
import br.all.domain.model.review.SystematicStudyId

class PreconditionChecker(
    private val reviewRepository: SystematicStudyRepository,
    private val credentialsService: ResearcherCredentialsService,
) {

    fun prepareIfViolatesPreconditions(presenter: GenericPresenter<*>, researcherId: ResearcherId, systematicStudyId: SystematicStudyId) {
        if (!credentialsService.isAuthenticated(researcherId)) {
            presenter.prepareFailView(UnauthenticatedUserException("User of id $researcherId is not authenticated."))
            return
        }
        if (!credentialsService.hasAuthority(researcherId)) {
            presenter.prepareFailView(UnauthorizedUserException("User of id $researcherId is not authorized."))
            return
        }
        if (!reviewRepository.existsById(systematicStudyId.value)) {
            presenter.prepareFailView(EntityNotFoundException("Review of id $systematicStudyId do not exists."))
            return
        }
        if (!reviewRepository.hasReviewer(systematicStudyId.value, researcherId.value))
            presenter.prepareFailView(UnauthorizedUserException("User of id $researcherId is not a reviewer."))
    }

    fun prepareIfViolatesPreconditions(
        presenter: GenericPresenter<*>,
        researcherId: ResearcherId,
        systematicStudyId: SystematicStudyId,
        protocolId: ProtocolId,
        protocolRepository: ProtocolRepository,
    ) {
        prepareIfViolatesPreconditions(presenter, researcherId, systematicStudyId)
        if (presenter.isDone()) return

        if(!protocolRepository.existsById(protocolId.value())) {
            presenter.prepareFailView(EntityNotFoundException("Protocol with id $protocolId does not exist!"))
            return
        }
        if (!protocolRepository.belongsToSystematicStudy(protocolId.value(), systematicStudyId.value()))
            presenter.prepareFailView(IllegalStateException("Protocol $protocolId does not belongs to study $systematicStudyId"))
    }
}