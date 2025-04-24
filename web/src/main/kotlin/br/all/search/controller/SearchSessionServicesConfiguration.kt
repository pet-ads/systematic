package br.all.search.controller

import br.all.application.protocol.repository.ProtocolRepository
import br.all.application.user.credentials.ResearcherCredentialsService
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.search.CreateSearchSessionServiceImpl
import br.all.application.search.delete.DeleteSearchSessionServiceImpl
import br.all.application.search.find.service.FindAllSearchSessionsBySourceService
import br.all.application.search.find.service.FindAllSearchSessionsBySourceServiceImpl
import br.all.application.search.find.service.FindSearchSessionServiceImpl
import br.all.application.search.find.service.FindAllSearchSessionsServiceImpl
import br.all.application.search.repository.SearchSessionRepository
import br.all.application.search.update.PatchSearchSessionPresenter
import br.all.application.search.update.PatchSearchSessionService
import br.all.application.search.update.PatchSearchSessionServiceImpl
import br.all.application.search.update.UpdateSearchSessionServiceImpl
import br.all.application.study.repository.StudyReviewRepository
import br.all.application.user.CredentialsService
import br.all.domain.services.*
import br.all.infrastructure.protocol.MongoProtocolRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SearchSessionServicesConfiguration {

    @Bean
    fun bibtexConverterService(idGenerator: IdGeneratorService) = BibtexConverterService(idGenerator)

    @Bean
    fun risConverterService(idGenerator: IdGeneratorService) = RisConverterService(idGenerator)

    @Bean
    fun converterFactoryService(idGenerator: IdGeneratorService) = ConverterFactoryService(
        bibtexConverterService(idGenerator),
        risConverterService(idGenerator)
    )

    @Bean
    fun patchSearchSessionService(
        systematicStudyRepository: SystematicStudyRepository,
        searchSessionRepository: SearchSessionRepository,
        credentialsService: CredentialsService,
        studyReviewRepository: StudyReviewRepository,
        converterFactoryService: ConverterFactoryService,
        protocolRepository: ProtocolRepository,
    ) = PatchSearchSessionServiceImpl(
        systematicStudyRepository,
        searchSessionRepository,
        credentialsService,
        studyReviewRepository,
        converterFactoryService,
        protocolRepository
    )

    @Bean
    fun createSearchSession(
        searchSessionRepository: SearchSessionRepository,
        systematicStudyRepository: SystematicStudyRepository,
        protocolRepository: ProtocolRepository,
        uuidGeneratorService: UuidGeneratorService,
        converterFactoryService: ConverterFactoryService,
        studyReviewRepository: StudyReviewRepository,
        credentialsService: CredentialsService
    ) = CreateSearchSessionServiceImpl(
        searchSessionRepository,
        systematicStudyRepository,
        protocolRepository,
        uuidGeneratorService,
        converterFactoryService,
        studyReviewRepository,
        credentialsService
    )

    @Bean
    fun findSearchSession(
        searchSessionRepository: SearchSessionRepository,
        systematicStudyRepository: SystematicStudyRepository,
        credentialsService: CredentialsService
    ) = FindSearchSessionServiceImpl (
        systematicStudyRepository,
        searchSessionRepository,
        credentialsService
    )

    @Bean
    fun findAllSessionService(
        searchSessionRepository: SearchSessionRepository,
        systematicStudyRepository: SystematicStudyRepository,
        credentialsService: CredentialsService
    ) = FindAllSearchSessionsServiceImpl (
        systematicStudyRepository, searchSessionRepository, credentialsService
    )

    @Bean
    fun findAllSearchSessionsBySourceService(
        searchSessionRepository: SearchSessionRepository,
        systematicStudyRepository: SystematicStudyRepository,
        credentialsService: CredentialsService
    ) = FindAllSearchSessionsBySourceServiceImpl (
        systematicStudyRepository, searchSessionRepository, credentialsService
    )

    @Bean
    fun updateSessionService(
        searchSessionRepository: SearchSessionRepository,
        systematicStudyRepository: SystematicStudyRepository,
        credentialsService: CredentialsService
    ) = UpdateSearchSessionServiceImpl (
        systematicStudyRepository, searchSessionRepository, credentialsService
    )

    @Bean
    fun deleteSearchSessionService(
        credentialsService: CredentialsService,
        searchSessionRepository: SearchSessionRepository,
        systematicStudyRepository: SystematicStudyRepository
    ) = DeleteSearchSessionServiceImpl(
        systematicStudyRepository,
        searchSessionRepository,
        credentialsService
    )

}