package br.all.study.controller

import br.all.application.collaboration.repository.CollaborationRepository
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
        idGenerator: IdGeneratorService,
        collaborationRepository: CollaborationRepository
    ) = CreateStudyReviewServiceImpl(
        systematicStudyRepository, studyReviewRepository, credentialsService, idGenerator, collaborationRepository
    )

    @Bean
    fun updateStudyReviewService(
        systematicStudyRepository: SystematicStudyRepository,
        studyReviewRepository: StudyReviewRepository,
        credentialsService: CredentialsService,
        collaborationRepository: CollaborationRepository
    ) = UpdateStudyReviewServiceImpl(
        systematicStudyRepository, studyReviewRepository, credentialsService, collaborationRepository
    )

    @Bean
    fun findAllReviewService(
        systematicStudyRepository: SystematicStudyRepository,
        studyReviewRepository: StudyReviewRepository,
        findAllStudyReviewsPresenter: FindAllStudyReviewsPresenter,
        credentialsService: CredentialsService,
        collaborationRepository: CollaborationRepository
    ) = FindAllStudyReviewsServiceImpl(
        systematicStudyRepository, studyReviewRepository, credentialsService, collaborationRepository
    )

    @Bean
    fun findAllReviewBySourceService(
        systematicStudyRepository: SystematicStudyRepository,
        studyReviewRepository: StudyReviewRepository,
        //protocolRepository: ProtocolRepository,
        findAllStudyReviewsBySourcePresenter: FindAllStudyReviewsBySourcePresenter,
        credentialsService: CredentialsService,
        collaborationRepository: CollaborationRepository
    ) = FindAllStudyReviewsBySourceServiceImpl(
        systematicStudyRepository, studyReviewRepository, credentialsService, collaborationRepository
    )

    @Bean
    fun findAllReviewBySessionService(
        systematicStudyRepository: SystematicStudyRepository,
        studyReviewRepository: StudyReviewRepository,
        credentialsService: CredentialsService,
        collaborationRepository: CollaborationRepository
    ) = FindAllStudyReviewsBySessionServiceImpl(
        systematicStudyRepository, studyReviewRepository, credentialsService, collaborationRepository
    )
    @Bean
    fun findReviewService(
        systematicStudyRepository: SystematicStudyRepository,
        studyReviewRepository: StudyReviewRepository,
        findStudyReviewPresenter: FindStudyReviewPresenter,
        credentialsService: CredentialsService,
        collaborationRepository: CollaborationRepository
    ) = FindStudyReviewServiceImpl(
        systematicStudyRepository, studyReviewRepository, credentialsService, collaborationRepository
    )

    @Bean
    fun findAllByAuthorService(
        systematicStudyRepository: SystematicStudyRepository,
        studyReviewRepository: StudyReviewRepository,
        credentialsService: CredentialsService,
        collaborationRepository: CollaborationRepository
    ) = FindAllStudyReviewsByAuthorServiceImpl(
        systematicStudyRepository, studyReviewRepository, credentialsService, collaborationRepository
    )


    @Bean
    fun updateReviewSelectionService(
        systematicStudyRepository: SystematicStudyRepository,
        studyReviewRepository: StudyReviewRepository,
        updateStudyReviewStatusPresenter: UpdateStudyReviewStatusPresenter,
        credentialsService: CredentialsService,
        collaborationRepository: CollaborationRepository
    ) = UpdateStudyReviewSelectionService(
        systematicStudyRepository, studyReviewRepository, credentialsService, collaborationRepository
    )

    @Bean
    fun updateReviewExtractionService(
        systematicStudyRepository: SystematicStudyRepository,
        studyReviewRepository: StudyReviewRepository,
        updateStudyReviewStatusPresenter: UpdateStudyReviewStatusPresenter,
        credentialsService: CredentialsService,
        collaborationRepository: CollaborationRepository
    ) = UpdateStudyReviewExtractionService(
        systematicStudyRepository, studyReviewRepository, credentialsService, collaborationRepository
    )

    @Bean
    fun updateReviewPriorityService(
        systematicStudyRepository: SystematicStudyRepository,
        studyReviewRepository: StudyReviewRepository,
        updateStudyReviewStatusPresenter: UpdateStudyReviewStatusPresenter,
        credentialsService: CredentialsService,
        collaborationRepository: CollaborationRepository
    ) = UpdateStudyReviewPriorityService(
        systematicStudyRepository, studyReviewRepository, credentialsService, collaborationRepository
    )

    @Bean
    fun markAsDuplicatedService(
        systematicStudyRepository: SystematicStudyRepository,
        studyReviewRepository: StudyReviewRepository,
        updateStudyReviewStatusPresenter: UpdateStudyReviewStatusPresenter,
        credentialsService: CredentialsService,
        collaborationRepository: CollaborationRepository
    ) = MarkAsDuplicatedServiceImpl(
        systematicStudyRepository, studyReviewRepository, credentialsService, collaborationRepository
    )

    @Bean
    fun answerQuestionService(
        studyReviewRepository: StudyReviewRepository,
        questionRepository: QuestionRepository,
        systematicStudyRepository: SystematicStudyRepository,
        credentialsService: CredentialsService,
        collaborationRepository: CollaborationRepository
    ) = AnswerQuestionImpl(
        studyReviewRepository,
        questionRepository,
        systematicStudyRepository,
        credentialsService,
        collaborationRepository
    )

    @Bean
    fun batchAnswerQuestionService(
        studyReviewRepository: StudyReviewRepository,
        questionRepository: QuestionRepository,
        systematicStudyRepository: SystematicStudyRepository,
        credentialsService: CredentialsService,
        collaborationRepository: CollaborationRepository
    ) = BatchAnswerQuestionServiceImpl(
        studyReviewRepository,
        questionRepository,
        systematicStudyRepository,
        credentialsService,
        collaborationRepository
    )

    @Bean
    fun removeCriteriaService(
        studyReviewRepository: StudyReviewRepository,
        systematicStudyRepository: SystematicStudyRepository,
        credentialsService: CredentialsService,
        collaborationRepository: CollaborationRepository
    ) = RemoveCriteriaServiceImpl(
        studyReviewRepository,
        systematicStudyRepository,
        credentialsService,
        collaborationRepository
    )
}