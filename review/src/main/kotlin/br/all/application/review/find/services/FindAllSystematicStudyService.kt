package br.all.application.review.find.services

import br.all.application.review.find.presenter.FindAllSystematicStudyPresenter
import br.all.application.review.repository.SystematicStudyDto
import java.util.*

interface FindAllSystematicStudyService {
    fun findAll(presenter: FindAllSystematicStudyPresenter, researcherId: UUID)

    data class ResponseModel(
        val researcherId: UUID,
        val systematicStudies: List<SystematicStudyDto>,
    )
}