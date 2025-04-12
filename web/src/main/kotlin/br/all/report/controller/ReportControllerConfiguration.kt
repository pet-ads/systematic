package br.all.report.controller

import br.all.application.protocol.repository.ProtocolRepository
import br.all.application.question.repository.QuestionRepository
import br.all.application.report.find.service.*
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.study.repository.StudyReviewRepository
import br.all.application.user.CredentialsService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ReportControllerConfiguration {

    @Bean
    fun findAnswersService(
        questionRepository: QuestionRepository,
        studyReviewRepository: StudyReviewRepository,
        credentialsService: CredentialsService,
    ) = FindAnswersServiceImpl(
        questionRepository,
        studyReviewRepository,
        credentialsService,
    )

    @Bean
    fun findCriteriaService(
        protocolRepository: ProtocolRepository,
        studyReviewRepository: StudyReviewRepository,
        credentialsService: CredentialsService,
    ) = FindCriteriaServiceImpl(
        protocolRepository,
        credentialsService,
        studyReviewRepository,
    )

    @Bean
    fun findSourceService(
        studyReviewRepository: StudyReviewRepository,
        credentialsService: CredentialsService,
    ) = FindSourceSerivceImpl(
        studyReviewRepository,
        credentialsService,
    )

    @Bean
    fun authorNetwork(
        credentialsService: CredentialsService,
        studyReviewRepository: StudyReviewRepository,
        systematicStudyRepository: SystematicStudyRepository,
    ) = AuthorNetworkServiceImpl(
        credentialsService,
        studyReviewRepository,
        systematicStudyRepository,
    )

    @Bean
    fun findKeywordsService(
        credentialsService: CredentialsService,
        protocolRepository: ProtocolRepository,
    ) = FindKeywordsServiceImpl(
        protocolRepository,
        credentialsService
    )
}