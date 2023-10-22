package br.all.application.review.find

import br.all.application.review.repository.SystematicStudyDto
import br.all.application.review.repository.SystematicStudyRepository
import java.util.*

class FindOneSystematicStudyService(private val systematicStudyRepository: SystematicStudyRepository) {
    fun findById(studyId: UUID) : SystematicStudyDto? = systematicStudyRepository.findById(studyId)

    fun existById(studyId: UUID) = systematicStudyRepository.findById(studyId) != null
}