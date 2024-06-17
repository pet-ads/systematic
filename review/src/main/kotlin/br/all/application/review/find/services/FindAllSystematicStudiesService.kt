package br.all.application.review.find.services

import br.all.application.review.find.presenter.FindAllSystematicStudyPresenter
import br.all.application.review.repository.SystematicStudyDto
import java.util.*

interface FindAllSystematicStudiesService {
    fun findAllByCollaborator(presenter: FindAllSystematicStudyPresenter, user: UUID)

    fun findAllByOwner(presenter: FindAllSystematicStudyPresenter, request: FindByOwnerRequest)

    data class FindByOwnerRequest(
        val userId: UUID,
        val ownerId: UUID,
    )

    data class ResponseModel(
        val userId: UUID,
        val systematicStudies: List<SystematicStudyDto>,
        val ownerId: UUID? = null,
    )
}
