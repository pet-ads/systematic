package br.all.application.review.update

import br.all.application.researcher.repository.ResearcherRepository
import br.all.application.review.repository.SystematicStudyDto
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.review.repository.fromDto
import br.all.application.review.repository.toDto
import br.all.domain.model.researcher.ResearcherId
import br.all.domain.model.review.SystematicStudy
import br.all.domain.shared.ddd.Notification
import br.all.domain.shared.utils.requireThatExists
import java.util.*

class ChangeSystematicStudyOwnerService(
    private val systematicStudyRepository: SystematicStudyRepository,
    private val researcherRepository: ResearcherRepository,
) {
    fun changeOwner(reviewId: UUID, newOwner: UUID) {
        val notification = Notification()

        if (!researcherRepository.existsById(newOwner))
            notification.addError("The id $newOwner does not belong to any existent researcher!")

        val possibleSystematicStudy = systematicStudyRepository.findById(reviewId)

        if(possibleSystematicStudy == null)
            notification.addError("Cannot find a systematic study with id: $reviewId")

        requireThatExists(notification.hasNoErrors()) { notification.message() }

        val dto = possibleSystematicStudy as SystematicStudyDto

        val systematicStudy = SystematicStudy
                .fromDto(dto)
                .also { it.changeOwner(ResearcherId(newOwner)) }

        systematicStudyRepository.create(systematicStudy.toDto())

        //TODO I believe that there is no need to return the full payload only to inform it is ok
        /*val newDto = systematicStudy.toDto()
        systematicStudyRepository.create(newDto)

        return newDto*/
    }
}