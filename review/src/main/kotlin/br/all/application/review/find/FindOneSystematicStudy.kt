package br.all.application.review.find

import br.all.application.review.repository.SystematicStudyDto
import br.all.application.review.repository.SystematicStudyRepository
import java.util.*

class FindOneSystematicStudy(private val systematicStudyRepository: SystematicStudyRepository) {
    fun findById(studyId: UUID) : SystematicStudyResponseModel {
        val studies = mutableListOf<SystematicStudyDto>()

        systematicStudyRepository.findById(studyId)?.let{
            studies.add(it)
        }

        return SystematicStudyResponseModel(studies)
    }

    fun existById(studyId: UUID) = systematicStudyRepository.findById(studyId) != null
}