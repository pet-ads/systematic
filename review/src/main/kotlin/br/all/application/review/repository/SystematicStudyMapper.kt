package br.all.application.review.repository

import br.all.application.review.create.SystematicStudyRequestModel
import br.all.domain.model.researcher.ResearcherId
import br.all.domain.model.review.SystematicStudyId
import br.all.domain.model.review.SystematicStudy
import java.util.*

fun SystematicStudy.toDto() = SystematicStudyDto(
    systematicStudyId.value,
    title,
    description,
    owner.value,
    collaborators.map { it.value }.toSet(),
)

fun SystematicStudy.Companion.fromRequestModel(id: UUID, requestModel: SystematicStudyRequestModel) : SystematicStudy {
    return SystematicStudy(
        SystematicStudyId(id),
        requestModel.title,
        requestModel.description,
        ResearcherId(requestModel.owner),
    )
}

fun SystematicStudy.Companion.fromDto(dto: SystematicStudyDto) = SystematicStudy(
    SystematicStudyId(dto.id),
    dto.title,
    dto.description,
    ResearcherId(dto.owner),
    dto.collaborators
        .map { ResearcherId(it) }
        .toMutableSet(),
)
