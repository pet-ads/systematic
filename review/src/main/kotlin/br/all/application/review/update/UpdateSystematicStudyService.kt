package br.all.application.review.update

import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.review.repository.fromDto
import br.all.application.review.repository.toDto
import br.all.domain.model.review.SystematicStudy
import br.all.domain.shared.utils.requireThatExists
import java.util.*

class UpdateSystematicStudyService(private val systematicStudyRepository: SystematicStudyRepository) {
    fun update(systematicStudyId: UUID, request: UpdateSystematicStudyRequestModel)  {
        //TODO esse require parece que complica mais que ajuda. Não sei. Não é melhor um if simples?
        val dto = requireThatExists({ systematicStudyRepository.findById(systematicStudyId) })
                                        { "Cannot find systematic study id with id: $systematicStudyId" }

        val systematicStudy = SystematicStudy.fromDto(dto).apply {
            title = request.title ?: title
            description = request.description ?: description
        }

        val newDto = systematicStudy.toDto()
        if (dto != newDto) systematicStudyRepository.create(newDto)
    }
}