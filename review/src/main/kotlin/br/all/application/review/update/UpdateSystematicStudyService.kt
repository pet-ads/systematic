package br.all.application.review.update

import br.all.application.review.repository.SystematicStudyDto
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.review.repository.fromDto
import br.all.application.review.repository.toDto
import br.all.domain.model.review.SystematicStudy
import br.all.domain.shared.utils.requireThatExists
import java.util.*

class UpdateSystematicStudyService(private val systematicStudyRepository: SystematicStudyRepository) {
    fun update(systematicStudyId: UUID, requestModel: UpdateSystematicStudyRequestModel) : SystematicStudyDto {
        val dto = requireThatExists({ systematicStudyRepository.findById(systematicStudyId) })
                                        { "Cannot find systematic study id with id: $systematicStudyId" }
        val systematicStudy = SystematicStudy.fromDto(dto)
        val (title, description) = requestModel

        if (title != null)
            systematicStudy.rename(title)
        if (description != null)
            systematicStudy.changeDescription(description)

        val newDto = systematicStudy.toDto()

        if (dto != newDto)
            systematicStudyRepository.create(newDto)

        return newDto
    }
}