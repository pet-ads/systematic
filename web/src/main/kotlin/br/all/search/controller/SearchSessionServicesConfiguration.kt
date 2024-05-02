package br.all.search.controller

import br.all.application.researcher.credentials.ResearcherCredentialsService
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.search.CreateSearchSessionServiceImpl
import br.all.application.search.find.service.FindAllSearchSessionsBySourceService
import br.all.application.search.find.service.FindAllSearchSessionsBySourceServiceImpl
import br.all.application.search.find.service.FindSearchSessionServiceImpl
import br.all.application.search.find.service.FindAllSearchSessionsServiceImpl
import br.all.application.search.repository.SearchSessionRepository
import br.all.application.search.update.UpdateSearchSessionService
import br.all.application.search.update.UpdateSearchSessionServiceImpl
import br.all.application.study.repository.StudyReviewRepository
import br.all.domain.services.BibtexConverterService
import br.all.domain.services.IdGeneratorService
import br.all.domain.services.UuidGeneratorService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SearchSessionServicesConfiguration {

    @Bean
    fun bibtexConverterService(idGenerator: IdGeneratorService) = BibtexConverterService(idGenerator)

    @Bean
    fun createSearchSession(
        searchSessionRepository: SearchSessionRepository,
        systematicStudyRepository: SystematicStudyRepository,
        uuidGeneratorService: UuidGeneratorService,
        bibtexConverterService: BibtexConverterService,
        studyReviewRepository: StudyReviewRepository,
        credentialsService: ResearcherCredentialsService
    ) = CreateSearchSessionServiceImpl(
        searchSessionRepository,
        systematicStudyRepository,
        uuidGeneratorService,
        bibtexConverterService,
        studyReviewRepository,
        credentialsService
    )

    @Bean
    fun findSearchSession(
        searchSessionRepository: SearchSessionRepository,
        systematicStudyRepository: SystematicStudyRepository,
        credentialsService: ResearcherCredentialsService
    ) = FindSearchSessionServiceImpl (
        systematicStudyRepository,
        searchSessionRepository,
        credentialsService
    )

    @Bean
    fun findAllSessionService(
        searchSessionRepository: SearchSessionRepository,
        systematicStudyRepository: SystematicStudyRepository,
        credentialsService: ResearcherCredentialsService
    ) = FindAllSearchSessionsServiceImpl (
        systematicStudyRepository, searchSessionRepository, credentialsService
    )

    @Bean
    fun findAllSearchSessionsBySourceService(
        searchSessionRepository: SearchSessionRepository,
        systematicStudyRepository: SystematicStudyRepository,
        credentialsService: ResearcherCredentialsService
    ) = FindAllSearchSessionsBySourceServiceImpl (
        systematicStudyRepository, searchSessionRepository, credentialsService
    )

    @Bean
    fun updateSessionService(
        searchSessionRepository: SearchSessionRepository,
        systematicStudyRepository: SystematicStudyRepository,
        credentialsService: ResearcherCredentialsService
    ) = UpdateSearchSessionServiceImpl (
        systematicStudyRepository, searchSessionRepository, credentialsService
    )
}