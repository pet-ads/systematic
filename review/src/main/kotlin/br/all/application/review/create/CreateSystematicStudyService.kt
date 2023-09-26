package br.all.application.review.create

import br.all.application.researcher.repository.ResearcherRepository
import br.all.application.review.repository.SystematicStudyDto
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.review.repository.fromRequestModel
import br.all.application.review.repository.toDto
import br.all.application.shared.CouldNotCreateEntityException
import br.all.domain.model.review.SystematicStudy
import br.all.domain.services.UuidGeneratorService
import br.all.domain.shared.ddd.Notification
import java.util.*

class CreateSystematicStudyService(
    private val systematicStudyRepository: SystematicStudyRepository,
    private val researcherRepository: ResearcherRepository,
    private val uuidGeneratorService: UuidGeneratorService,
) {

    fun create(requestModel: SystematicStudyRequestModel): SystematicStudyDto {
        val notification = Notification()

        if (!researcherRepository.exists(requestModel.owner))
            notification.addError("Could not find a resercher ID for the owner: ${requestModel.owner}!")

        //TODO: This will become invites to colaborators, not direct insertions into StudyReview
        requestModel.collaborators
            .filter { !researcherRepository.exists(it) }
            .forEach { notification.addError("There is no researcher with id: $it!") }

        require(notification.hasNoErrors()) { notification.message() }

        val id = uuidGeneratorService.next()
        val systematicStudy = SystematicStudy.fromRequestModel(id, requestModel)
        systematicStudyRepository.create(systematicStudy.toDto())

        //TODO: Is there any reason for throwing here? Is it a data issue?
        return systematicStudyRepository.findById(id).orElseThrow {
            CouldNotCreateEntityException("Could not create a systematic study with such data!")
        }
    }
}