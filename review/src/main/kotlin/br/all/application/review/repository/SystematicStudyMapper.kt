package br.all.application.review.repository

import br.all.application.review.create.CreateSystematicStudyService.RequestModel
import br.all.domain.model.collaboration.CollaborationId
import br.all.domain.model.user.ResearcherId
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
    CollaborationId(requestModel.userId),
)

fun SystematicStudy.Companion.fromDto(dto: SystematicStudyDto) = SystematicStudy(
    SystematicStudyId(dto.id),
    dto.title,
    dto.description,
    CollaborationId(dto.owner),
    dto.collaborators
        .map { CollaborationId(it) }
        .toMutableSet(),
)
