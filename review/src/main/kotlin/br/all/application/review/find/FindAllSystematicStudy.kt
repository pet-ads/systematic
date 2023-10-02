package br.all.application.review.find

import br.all.application.review.repository.SystematicStudyRepository

class FindAllSystematicStudy(private val systematicStudyRepository: SystematicStudyRepository) {
    fun findAll() = SystematicStudyResponseModel(systematicStudyRepository.findAll())
}