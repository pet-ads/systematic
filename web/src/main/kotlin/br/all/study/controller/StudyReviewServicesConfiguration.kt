package br.all.study.controller

import br.all.application.question.repository.QuestionRepository
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.study.create.CreateStudyReviewServiceImpl
import br.all.application.study.find.presenter.FindAllStudyReviewsBySourcePresenter
import br.all.application.study.find.presenter.FindAllStudyReviewsPresenter
import br.all.application.study.find.presenter.FindStudyReviewPresenter
import br.all.application.study.find.service.*
import br.all.application.study.repository.StudyReviewRepository
import br.all.application.study.update.implementation.*
import br.all.application.study.update.interfaces.UpdateStudyReviewStatusPresenter
import br.all.application.user.CredentialsService
import br.all.domain.services.IdGeneratorService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class StudyReviewServicesConfiguration {

    @Bean
    fun createStudyReviewService(
        systematicStudyRepository: SystematicStudyRepository,
        studyReviewRepository: StudyReviewRepository,
        credentialsService: CredentialsService,
        idGenerator: IdGeneratorService
    ) = CreateStudyReviewServiceImpl(
        systematicStudyRepository, studyReviewRepository, credentialsService, idGenerator
    )

    @Bean
    fun updateStudyReviewService(
        systematicStudyRepository: SystematicStudyRepository,
        studyReviewRepository: StudyReviewRepository,
        credentialsService: CredentialsService,
    ) = UpdateStudyReviewServiceImpl(
        systematicStudyRepository, studyReviewRepository, credentialsService,
    )

    @Bean
    fun findAllReviewService(
        systematicStudyRepository: SystematicStudyRepository,
        studyReviewRepository: StudyReviewRepository,
        findAllStudyReviewsPresenter: FindAllStudyReviewsPresenter,
        credentialsService: CredentialsService
    ) = FindAllStudyReviewsServiceImpl(
        systematicStudyRepository, studyReviewRepository, credentialsService
    )

    @Bean
    fun findAllReviewBySourceService(
        systematicStudyRepository: SystematicStudyRepository,
        studyReviewRepository: StudyReviewRepository,
        //protocolRepository: ProtocolRepository,
        findAllStudyReviewsBySourcePresenter: FindAllStudyReviewsBySourcePresenter,
        credentialsService: CredentialsService
    ) = FindAllStudyReviewsBySourceServiceImpl(
        systematicStudyRepository, studyReviewRepository, credentialsService
    )

    @Bean
    fun findAllReviewBySessionService(
        systematicStudyRepository: SystematicStudyRepository,
        studyReviewRepository: StudyReviewRepository,
        credentialsService: CredentialsService
    ) = FindAllStudyReviewsBySessionServiceImpl(
        systematicStudyRepository, studyReviewRepository, credentialsService
    )
    @Bean
    fun findReviewService(
        systematicStudyRepository: SystematicStudyRepository,
        studyReviewRepository: StudyReviewRepository,
        findStudyReviewPresenter: FindStudyReviewPresenter,
        credentialsService: CredentialsService
    ) = FindStudyReviewServiceImpl(
        systematicStudyRepository, studyReviewRepository, credentialsService
    )

    @Bean
    fun updateReviewSelectionService(
        systematicStudyRepository: SystematicStudyRepository,
        studyReviewRepository: StudyReviewRepository,
        updateStudyReviewStatusPresenter: UpdateStudyReviewStatusPresenter,
        credentialsService: CredentialsService
    ) = UpdateStudyReviewSelectionService(
        systematicStudyRepository, studyReviewRepository, credentialsService
    )

    @Bean
    fun updateReviewExtractionService(
        systematicStudyRepository: SystematicStudyRepository,
        studyReviewRepository: StudyReviewRepository,
        updateStudyReviewStatusPresenter: UpdateStudyReviewStatusPresenter,
        credentialsService: CredentialsService
    ) = UpdateStudyReviewExtractionService(
        systematicStudyRepository, studyReviewRepository, credentialsService
    )

    @Bean
    fun updateReviewPriorityService(
        systematicStudyRepository: SystematicStudyRepository,
        studyReviewRepository: StudyReviewRepository,
        updateStudyReviewStatusPresenter: UpdateStudyReviewStatusPresenter,
        credentialsService: CredentialsService
    ) = UpdateStudyReviewPriorityService(
        systematicStudyRepository, studyReviewRepository, credentialsService
    )

    @Bean
    fun markAsDuplicatedService(
        systematicStudyRepository: SystematicStudyRepository,
        studyReviewRepository: StudyReviewRepository,
        updateStudyReviewStatusPresenter: UpdateStudyReviewStatusPresenter,
        credentialsService: CredentialsService
    ) = MarkAsDuplicatedServiceImpl(
        systematicStudyRepository, studyReviewRepository, credentialsService
    )

    @Bean
    fun riskOfBiasService(
        studyReviewRepository: StudyReviewRepository,
        questionRepository: QuestionRepository,
        systematicStudyRepository: SystematicStudyRepository,
        credentialsService: CredentialsService,
    ) = AnswerRiskOfBiasQuestionImpl(
        studyReviewRepository,
        questionRepository,
        systematicStudyRepository,
        credentialsService
    )
}