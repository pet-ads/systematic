package br.all.application.review.create

import br.all.application.researcher.repository.ResearcherRepository
import br.all.application.review.repository.SystematicStudyDto
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.review.repository.fromRequestModel
import br.all.application.review.repository.toDto
import br.all.application.shared.CouldNotCreateEntityException
import br.all.domain.model.review.SystematicStudy
import br.all.domain.services.UuidGeneratorService

class CreateSystematicStudyService(
    private val systematicStudyRepository : SystematicStudyRepository,
    private val researcherRepository: ResearcherRepository,
    private val uuidGeneratorService : UuidGeneratorService,
) {
    fun create(requestModel : SystematicStudyRequestModel) : SystematicStudyDto {
        val id = uuidGeneratorService.next()
        val systematicStudy = SystematicStudy.fromRequestModel(id, requestModel)
        systematicStudyRepository.create(systematicStudy.toDto())

        return systematicStudyRepository.findById(id)
                        .orElseThrow { CouldNotCreateEntityException("Could not create a systematic study with " +
                                "such data!")
                        }
    }
}