package br.all.search.controller

import br.all.application.researcher.credentials.ResearcherCredentialsService
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.search.CreateSearchSessionServiceImpl
import br.all.application.search.repository.SearchSessionRepository
import br.all.application.study.create.CreateStudyReviewServiceImpl
import br.all.application.study.repository.StudyReviewRepository
import br.all.domain.services.BibtexConverterService
import br.all.domain.services.IdGeneratorService
import br.all.domain.services.UuidGeneratorService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SearchSessionServicesConfiguration {

    @Bean
    fun createSearchSession(
        searchSessionRepository: SearchSessionRepository,
        systematicStudyRepository: SystematicStudyRepository,
        uuidGeneratorService: UuidGeneratorService,
        bibtexConverterService: BibtexConverterService
    ) = CreateSearchSessionServiceImpl(
        searchSessionRepository, systematicStudyRepository, uuidGeneratorService, bibtexConverterService
    )
}