package br.all.study.controller

import br.all.application.researcher.credentials.ResearcherCredentialsService
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.study.create.CreateStudyReviewPresenter
import br.all.application.study.create.CreateStudyReviewServiceImpl
import br.all.application.study.find.presenter.FindAllStudyReviewsPresenter
import br.all.application.study.find.service.FindAllStudyReviewsServiceImpl
import br.all.application.study.find.presenter.FindStudyReviewPresenter
import br.all.application.study.find.service.FindStudyReviewServiceImpl
import br.all.application.study.repository.StudyReviewRepository
import br.all.domain.services.IdGeneratorService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class StudyReviewServicesConfiguration {

    @Bean
    fun createStudyReviewService(
        systematicStudyRepository: SystematicStudyRepository,
        studyReviewRepository: StudyReviewRepository,
        createStudyReviewPresenter: CreateStudyReviewPresenter,
        credentialsService: ResearcherCredentialsService,
        idGenerator: IdGeneratorService
    ) = CreateStudyReviewServiceImpl(
        systematicStudyRepository, studyReviewRepository, createStudyReviewPresenter, credentialsService, idGenerator
    )

    @Bean
    fun findAllReviewService(
        systematicStudyRepository: SystematicStudyRepository,
        studyReviewRepository: StudyReviewRepository,
        findAllStudyReviewsPresenter: FindAllStudyReviewsPresenter,
        credentialsService: ResearcherCredentialsService
    ) = FindAllStudyReviewsServiceImpl(
        systematicStudyRepository, studyReviewRepository, findAllStudyReviewsPresenter, credentialsService
    )

    @Bean
    fun findReviewService(
        systematicStudyRepository: SystematicStudyRepository,
        studyReviewRepository: StudyReviewRepository,
        findStudyReviewPresenter: FindStudyReviewPresenter,
        credentialsService: ResearcherCredentialsService
    ) = FindStudyReviewServiceImpl(
        systematicStudyRepository, studyReviewRepository, findStudyReviewPresenter, credentialsService
    )
}