package br.all.application.review.repository

import br.all.application.review.create.CreateSystematicStudyService.RequestModel
import br.all.domain.shared.user.ResearcherId
import br.all.domain.model.review.SystematicStudy
import br.all.domain.model.review.SystematicStudyId
import java.util.*

fun SystematicStudy.toDto() = SystematicStudyDto(
    id.value(),
    title,
    description,
    owner.value,
    collaborators.map { it.value }.toSet(),
)

fun SystematicStudy.Companion.fromRequestModel(id: UUID, requestModel: RequestModel) = SystematicStudy(
    SystematicStudyId(id),
    requestModel.title,
    requestModel.description,
    ResearcherId(requestModel.userId),
)

fun SystematicStudy.Companion.fromDto(dto: SystematicStudyDto) = SystematicStudy(
    SystematicStudyId(dto.id),
    dto.title,
    dto.description,
    ResearcherId(dto.owner),
    dto.collaborators
        .map { ResearcherId(it) }
        .toMutableSet(),
)
