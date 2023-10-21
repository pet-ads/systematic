package br.all.application.review.find

import br.all.application.review.repository.SystematicStudyDto
import br.all.application.review.repository.SystematicStudyRepository
import java.util.*

class FindOneSystematicStudyService(private val systematicStudyRepository: SystematicStudyRepository) {

    //TODO: Why find one is returning a list as a reponse model? How about return SystematicStudyDto instead
    // ou null instead?

    fun findById(studyId: UUID) : SystematicStudyResponseModel {
        val studies = mutableListOf<SystematicStudyDto>()

        systematicStudyRepository.findById(studyId)?.let{
            studies.add(it)
        }

        return SystematicStudyResponseModel(studies)
    }

    fun existById(studyId: UUID) = systematicStudyRepository.findById(studyId) != null
}