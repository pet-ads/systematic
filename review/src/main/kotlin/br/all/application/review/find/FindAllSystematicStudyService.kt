package br.all.application.review.find

import br.all.application.review.repository.SystematicStudyRepository

class FindAllSystematicStudyService(private val systematicStudyRepository: SystematicStudyRepository) {
    fun findAll() = FindSystematicStudyResponseModel(systematicStudyRepository.findAll())
}