package br.all.report.controller

import br.all.application.protocol.repository.ProtocolRepository
import br.all.application.question.repository.QuestionRepository
import br.all.application.report.find.service.*
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.study.repository.StudyReviewRepository
import br.all.application.user.CredentialsService
import br.all.domain.services.CsvFormatterService
import br.all.domain.services.FormatterFactoryService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ReportControllerConfiguration {
    @Bean
    fun csvFormatterService() = CsvFormatterService()

    @Bean
    fun formatterFactoryService(
        csvFormatterService: CsvFormatterService
    ) = FormatterFactoryService(
        csvFormatterService
    )

    @Bean
    fun includedStudiesAnswersService(
        questionRepository: QuestionRepository,
        studyReviewRepository: StudyReviewRepository,
        credentialsService: CredentialsService,
        systematicStudyRepository: SystematicStudyRepository,
    ) = IncludedStudiesAnswersServiceImpl(
        questionRepository,
        studyReviewRepository,
        credentialsService,
        systematicStudyRepository,
    )

    @Bean
    fun findCriteriaService(
        protocolRepository: ProtocolRepository,
        studyReviewRepository: StudyReviewRepository,
        credentialsService: CredentialsService,
        systematicStudyRepository: SystematicStudyRepository
    ) = FindCriteriaServiceImpl(
        protocolRepository,
        systematicStudyRepository,
        credentialsService,
        studyReviewRepository,
    )

    @Bean
    fun findSourceService(
        protocolRepository: ProtocolRepository,
        systematicStudyRepository: SystematicStudyRepository,
        studyReviewRepository: StudyReviewRepository,
        credentialsService: CredentialsService,
    ) = FindSourceServiceImpl(
        protocolRepository,
        systematicStudyRepository,
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
        systematicStudyRepository: SystematicStudyRepository,
        studyReviewRepository: StudyReviewRepository,
    ) = FindKeywordsServiceImpl(
        systematicStudyRepository,
        studyReviewRepository,
        credentialsService
    )

    @Bean
    fun exportProtocolService(
        credentialsService: CredentialsService,
        systematicStudyRepository: SystematicStudyRepository,
        formatterFactoryService: FormatterFactoryService,
        protocolRepository: ProtocolRepository,
    ) = ExportProtocolServiceImpl(
        credentialsService,
        systematicStudyRepository,
        formatterFactoryService,
        protocolRepository
    )

    @Bean
    fun findStudiesByStage(
        credentialsService: CredentialsService,
        studyReviewRepository: StudyReviewRepository,
        systematicStudyRepository: SystematicStudyRepository,
    ) = FindStudiesByStageServiceImpl(
        credentialsService,
        studyReviewRepository,
        systematicStudyRepository,
    )

    @Bean
    fun studiesFunnelService(
        credentialsService: CredentialsService,
        studyReviewRepository: StudyReviewRepository,
        systematicStudyRepository: SystematicStudyRepository,
    ) = StudiesFunnelServiceImpl(
        credentialsService,
        studyReviewRepository,
        systematicStudyRepository,
    )

    @Bean
    fun findAnswerService(
        credentialsService: CredentialsService,
        studyReviewRepository: StudyReviewRepository,
        systematicStudyRepository: SystematicStudyRepository,
        questionRepository: QuestionRepository
    ) = FindAnswerServiceImpl(
        credentialsService,
        studyReviewRepository,
        systematicStudyRepository,
        questionRepository
    )
}