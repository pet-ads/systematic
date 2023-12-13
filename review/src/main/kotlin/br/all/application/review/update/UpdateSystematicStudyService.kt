package br.all.application.review.update

import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.review.repository.fromDto
import br.all.application.review.repository.toDto
import br.all.application.review.shared.SystematicStudyResponseModel
import br.all.domain.model.review.SystematicStudy
import br.all.domain.shared.utils.requireThatExists
import java.util.*

class UpdateSystematicStudyService(private val systematicStudyRepository: SystematicStudyRepository) {
    fun update(systematicStudyId: UUID, request: UpdateSystematicStudyRequestModel): SystematicStudyResponseModel {
        val dto = requireThatExists(systematicStudyRepository.findById(systematicStudyId))
                        { "Cannot find systematic study id with id: $systematicStudyId" }

        val systematicStudy = SystematicStudy.fromDto(dto).apply {
            title = request.title ?: title
            description = request.description ?: description
        }
        val newDto = systematicStudy.toDto()
        
        if (dto != newDto) systematicStudyRepository.saveOrUpdate(newDto)
        
        return SystematicStudyResponseModel(systematicStudyId)
    }
}