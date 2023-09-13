package br.all.application.review.repository

import br.all.application.review.create.SystematicStudyRequestModel
import br.all.domain.model.researcher.ResearcherId
import br.all.domain.model.review.ReviewId
import br.all.domain.model.review.SystematicStudy
import java.util.*

fun SystematicStudy.toDto() = SystematicStudyDto(
    id.value,
    title,
    description,
    researchers.map { it.value }
        .toSet(),
)

fun SystematicStudy.Companion.fromRequestModel(id: UUID, requestModel: SystematicStudyRequestModel) : SystematicStudy {
    return SystematicStudy(
        ReviewId(id),
        requestModel.title,
        requestModel.description,
        requestModel.researchers
            .map { ResearcherId(it) }
            .toMutableSet(),
    )
}
