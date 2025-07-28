package br.all.report.controller

import br.all.application.collaboration.repository.CollaborationRepository
import br.all.application.protocol.repository.ProtocolRepository
import br.all.application.question.repository.QuestionRepository
import br.all.application.report.find.service.*
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.study.repository.StudyReviewRepository
import br.all.application.user.CredentialsService
import br.all.domain.services.CsvFormatterService
import br.all.domain.services.FormatterFactoryService
import br.all.domain.services.LatexFormatterService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ReportControllerConfiguration {
    @Bean
    fun csvFormatterService() = CsvFormatterService()

    @Bean
    fun latexFormatterService() = LatexFormatterService()

    @Bean
    fun formatterFactoryService(
        csvFormatterService: CsvFormatterService,
        latexFormatterService: LatexFormatterService
    ) = FormatterFactoryService(
        csvFormatterService,
        latexFormatterService
    )

    @Bean
    fun includedStudiesAnswersService(
        questionRepository: QuestionRepository,
        studyReviewRepository: StudyReviewRepository,
        credentialsService: CredentialsService,
        systematicStudyRepository: SystematicStudyRepository,
        collaborationRepository: CollaborationRepository
    ) = IncludedStudiesAnswersServiceImpl(
        questionRepository,
        studyReviewRepository,
        credentialsService,
        systematicStudyRepository,
        collaborationRepository
    )

    @Bean
    fun findCriteriaService(
        protocolRepository: ProtocolRepository,
        studyReviewRepository: StudyReviewRepository,
        credentialsService: CredentialsService,
        systematicStudyRepository: SystematicStudyRepository,
        collaborationRepository: CollaborationRepository
    ) = FindCriteriaServiceImpl(
        protocolRepository,
        systematicStudyRepository,
        credentialsService,
        studyReviewRepository,
        collaborationRepository
    )

    @Bean
    fun findSourceService(
        protocolRepository: ProtocolRepository,
        systematicStudyRepository: SystematicStudyRepository,
        studyReviewRepository: StudyReviewRepository,
        credentialsService: CredentialsService,
        collaborationRepository: CollaborationRepository
    ) = FindSourceServiceImpl(
        protocolRepository,
        systematicStudyRepository,
        studyReviewRepository,
        credentialsService,
        collaborationRepository
    )

    @Bean
    fun authorNetwork(
        credentialsService: CredentialsService,
        studyReviewRepository: StudyReviewRepository,
        systematicStudyRepository: SystematicStudyRepository,
        collaborationRepository: CollaborationRepository
    ) = AuthorNetworkServiceImpl(
        credentialsService,
        studyReviewRepository,
        systematicStudyRepository,
        collaborationRepository
    )

    @Bean
    fun findKeywordsService(
        credentialsService: CredentialsService,
        systematicStudyRepository: SystematicStudyRepository,
        studyReviewRepository: StudyReviewRepository,
        collaborationRepository: CollaborationRepository
    ) = FindKeywordsServiceImpl(
        systematicStudyRepository,
        studyReviewRepository,
        credentialsService,
        collaborationRepository
    )

    @Bean
    fun exportProtocolService(
        credentialsService: CredentialsService,
        systematicStudyRepository: SystematicStudyRepository,
        formatterFactoryService: FormatterFactoryService,
        protocolRepository: ProtocolRepository,
        collaborationRepository: CollaborationRepository
    ) = ExportProtocolServiceImpl(
        credentialsService,
        systematicStudyRepository,
        formatterFactoryService,
        protocolRepository,
        collaborationRepository
    )

    @Bean
    fun findStudiesByStage(
        credentialsService: CredentialsService,
        studyReviewRepository: StudyReviewRepository,
        systematicStudyRepository: SystematicStudyRepository,
        collaborationRepository: CollaborationRepository
    ) = FindStudiesByStageServiceImpl(
        credentialsService,
        studyReviewRepository,
        systematicStudyRepository,
        collaborationRepository
    )

    @Bean
    fun studiesFunnelService(
        credentialsService: CredentialsService,
        studyReviewRepository: StudyReviewRepository,
        systematicStudyRepository: SystematicStudyRepository,
        collaborationRepository: CollaborationRepository
    ) = StudiesFunnelServiceImpl(
        credentialsService,
        studyReviewRepository,
        systematicStudyRepository,
        collaborationRepository
    )

    @Bean
    fun findAnswerService(
        credentialsService: CredentialsService,
        studyReviewRepository: StudyReviewRepository,
        systematicStudyRepository: SystematicStudyRepository,
        questionRepository: QuestionRepository,
        collaborationRepository: CollaborationRepository
    ) = FindAnswerServiceImpl(
        credentialsService,
        studyReviewRepository,
        systematicStudyRepository,
        questionRepository,
        collaborationRepository
    )

    @Bean
    fun findStudyReviewCriteriaService(
        systematicStudyRepository: SystematicStudyRepository,
        studyReviewRepository: StudyReviewRepository,
        credentialsService: CredentialsService,
        protocolRepository: ProtocolRepository,
        collaborationRepository: CollaborationRepository
    ) = FindStudyReviewCriteriaServiceImpl(
        systematicStudyRepository,
        studyReviewRepository,
        credentialsService,
        protocolRepository,
        collaborationRepository
    )
}