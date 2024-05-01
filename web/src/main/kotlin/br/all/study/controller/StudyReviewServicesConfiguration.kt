package br.all.study.controller

import br.all.application.protocol.repository.ProtocolRepository
import br.all.application.question.repository.QuestionRepository
import br.all.application.researcher.credentials.ResearcherCredentialsService
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.study.create.CreateStudyReviewServiceImpl
import br.all.application.study.find.presenter.FindAllStudyReviewsBySourcePresenter
import br.all.application.study.find.presenter.FindAllStudyReviewsPresenter
import br.all.application.study.find.service.FindAllStudyReviewsServiceImpl
import br.all.application.study.find.presenter.FindStudyReviewPresenter
import br.all.application.study.find.service.FindAllStudyReviewsBySourceServiceImpl
import br.all.application.study.find.service.FindStudyReviewServiceImpl
import br.all.application.study.repository.StudyReviewRepository
import br.all.application.study.update.implementation.*
import br.all.application.study.update.interfaces.UpdateStudyReviewStatusPresenter
import br.all.domain.services.IdGeneratorService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import br.all.application.study.update.implementation.AnswerRiskOfBiasQuestionImpl


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
    fun updateStudyReviewService(
        systematicStudyRepository: SystematicStudyRepository,
        studyReviewRepository: StudyReviewRepository,
        credentialsService: ResearcherCredentialsService,
    ) = UpdateStudyReviewServiceImpl(
        systematicStudyRepository, studyReviewRepository, credentialsService,
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
    fun findAllReviewBySourceService(
        systematicStudyRepository: SystematicStudyRepository,
        studyReviewRepository: StudyReviewRepository,
        //protocolRepository: ProtocolRepository,
        findAllStudyReviewsBySourcePresenter: FindAllStudyReviewsBySourcePresenter,
        credentialsService: ResearcherCredentialsService
    ) = FindAllStudyReviewsBySourceServiceImpl(
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
    fun updateReviewSelectionService(
        systematicStudyRepository: SystematicStudyRepository,
        studyReviewRepository: StudyReviewRepository,
        updateStudyReviewStatusPresenter: UpdateStudyReviewStatusPresenter,
        credentialsService: ResearcherCredentialsService
    ) = UpdateStudyReviewSelectionService(
        systematicStudyRepository, studyReviewRepository, credentialsService
    )

    @Bean
    fun updateReviewExtractionService(
        systematicStudyRepository: SystematicStudyRepository,
        studyReviewRepository: StudyReviewRepository,
        updateStudyReviewStatusPresenter: UpdateStudyReviewStatusPresenter,
        credentialsService: ResearcherCredentialsService
    ) = UpdateStudyReviewExtractionService(
        systematicStudyRepository, studyReviewRepository, credentialsService
    )

    @Bean
    fun updateReviewPriorityService(
        systematicStudyRepository: SystematicStudyRepository,
        studyReviewRepository: StudyReviewRepository,
        updateStudyReviewStatusPresenter: UpdateStudyReviewStatusPresenter,
        credentialsService: ResearcherCredentialsService
    ) = UpdateStudyReviewPriorityService(
        systematicStudyRepository, studyReviewRepository, credentialsService
    )

    @Bean
    fun markAsDuplicatedService(
        systematicStudyRepository: SystematicStudyRepository,
        studyReviewRepository: StudyReviewRepository,
        updateStudyReviewStatusPresenter: UpdateStudyReviewStatusPresenter,
        credentialsService: ResearcherCredentialsService
    ) = MarkAsDuplicatedServiceImpl(
        systematicStudyRepository, studyReviewRepository, credentialsService
    )

    @Bean
    fun answerRiskOfBiasQuestionService(
        studyReviewRepository: StudyReviewRepository,
        questionRepository: QuestionRepository,
        systematicStudyRepository: SystematicStudyRepository,
        credentialsService: ResearcherCredentialsService
    ) = AnswerRiskOfBiasQuestionImpl(studyReviewRepository, questionRepository,
        systematicStudyRepository, credentialsService)
}