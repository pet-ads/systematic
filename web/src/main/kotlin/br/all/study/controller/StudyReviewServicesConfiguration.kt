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
import br.all.application.study.update.implementation.UpdateStudyReviewExtractionService
import br.all.application.study.update.implementation.UpdateStudyReviewPriorityService
import br.all.application.study.update.implementation.UpdateStudyReviewSelectionService
import br.all.application.study.update.interfaces.UpdateStudyReviewStatusPresenter
import br.all.domain.services.IdGeneratorService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class StudyReviewServicesConfiguration {

    @Bean
    fun createStudyReviewService(
        systematicStudyRepository: SystematicStudyRepository,
        studyReviewRepository: StudyReviewRepository,
        credentialsService: ResearcherCredentialsService,
        idGenerator: IdGeneratorService
    ) = CreateStudyReviewServiceImpl(
        systematicStudyRepository, studyReviewRepository, credentialsService, idGenerator
    )

    @Bean
    fun findAllReviewService(
        systematicStudyRepository: SystematicStudyRepository,
        studyReviewRepository: StudyReviewRepository,
        findAllStudyReviewsPresenter: FindAllStudyReviewsPresenter,
        credentialsService: ResearcherCredentialsService
    ) = FindAllStudyReviewsServiceImpl(
        systematicStudyRepository, studyReviewRepository, credentialsService
    )

    @Bean
    fun findReviewService(
        systematicStudyRepository: SystematicStudyRepository,
        studyReviewRepository: StudyReviewRepository,
        findStudyReviewPresenter: FindStudyReviewPresenter,
        credentialsService: ResearcherCredentialsService
    ) = FindStudyReviewServiceImpl(
        systematicStudyRepository, studyReviewRepository, credentialsService
    )

    @Bean
    fun updateReviewServiceSelection(
        systematicStudyRepository: SystematicStudyRepository,
        studyReviewRepository: StudyReviewRepository,
        updateStudyReviewStatusPresenter: UpdateStudyReviewStatusPresenter,
        credentialsService: ResearcherCredentialsService
    ) = UpdateStudyReviewSelectionService(
        systematicStudyRepository, studyReviewRepository, credentialsService
    )

    @Bean
    fun updateReviewServiceExtraction(
        systematicStudyRepository: SystematicStudyRepository,
        studyReviewRepository: StudyReviewRepository,
        updateStudyReviewStatusPresenter: UpdateStudyReviewStatusPresenter,
        credentialsService: ResearcherCredentialsService
    ) = UpdateStudyReviewExtractionService(
        systematicStudyRepository, studyReviewRepository, credentialsService
    )

    @Bean
    fun updateReviewServicePriority(
        systematicStudyRepository: SystematicStudyRepository,
        studyReviewRepository: StudyReviewRepository,
        updateStudyReviewStatusPresenter: UpdateStudyReviewStatusPresenter,
        credentialsService: ResearcherCredentialsService
    ) = UpdateStudyReviewPriorityService(
        systematicStudyRepository, studyReviewRepository, credentialsService
    )
}