package br.all.application.review.find

import br.all.application.review.repository.SystematicStudyRepository
import java.util.*

class FindOneSystematicStudy(private val systematicStudyRepository: SystematicStudyRepository) {
    fun findById(studyId: UUID) : SystematicStudyResponseModel {
        val foundStudy = systematicStudyRepository.findById(studyId) ?:
                            throw NoSuchElementException("Could not find a systematic study with id: $studyId")
        return SystematicStudyResponseModel(listOf(foundStudy))
    }

    fun existById(studyId: UUID) = systematicStudyRepository.findById(studyId) != null
}