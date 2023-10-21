package br.all.application.review.create

import br.all.application.researcher.repository.ResearcherRepository
import br.all.application.review.repository.SystematicStudyDto
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.review.repository.fromRequestModel
import br.all.application.review.repository.toDto
import br.all.domain.model.review.SystematicStudy
import br.all.domain.services.UuidGeneratorService
import br.all.domain.shared.utils.requireThatExists

class CreateSystematicStudyService(
    private val systematicStudyRepository: SystematicStudyRepository,
    private val researcherRepository: ResearcherRepository,
    private val uuidGeneratorService: UuidGeneratorService,
) {
    fun create(requestModel: SystematicStudyRequestModel): SystematicStudyDto{

        if(!researcherRepository.existsById(requestModel.owner))
            throw NoSuchElementException("Could not find a researcher ID for the owner: ${requestModel.owner}!")

        val id = uuidGeneratorService.next()
        val systematicStudyDto = SystematicStudy.fromRequestModel(id, requestModel).toDto()
        systematicStudyRepository.create(systematicStudyDto)

        return systematicStudyDto
    }
}