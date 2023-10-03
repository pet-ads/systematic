package br.all.application.review.update

import br.all.application.researcher.repository.ResearcherRepository
import br.all.application.review.repository.SystematicStudyDto
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.review.repository.fromDto
import br.all.application.review.repository.toDto
import br.all.application.shared.utils.requireThatExists
import br.all.domain.model.researcher.ResearcherId
import br.all.domain.model.review.SystematicStudy
import br.all.domain.shared.ddd.Notification
import java.util.*

class ChangeSystematicStudyOwnerService(
    private val systematicStudyRepository: SystematicStudyRepository,
    private val researcherRepository: ResearcherRepository,
) {
    fun changeOwner(reviewId: UUID, newOwner: UUID) : SystematicStudyDto {
        val dto = tryToFindSystematicStudy(newOwner, reviewId)
        val systematicStudy = SystematicStudy.fromDto(dto)

        systematicStudy.changeOwner(ResearcherId(newOwner))
        val newDto = systematicStudy.toDto()
        systematicStudyRepository.create(newDto)

        return newDto
    }

    private fun tryToFindSystematicStudy(newOwner: UUID, reviewId: UUID) : SystematicStudyDto {
        val notification = Notification()

        if (researcherRepository.existsById(newOwner))
            notification.addError("The id $newOwner does not belong to any existent researcher!")
        val possibleSystematicStudy = systematicStudyRepository.findById(reviewId) ?: run {
            notification.addError("Cannot find a systematic study with id: $reviewId")
        }

        requireThatExists(notification.hasNoErrors()) { notification.message() }

        return possibleSystematicStudy as SystematicStudyDto
    }
}