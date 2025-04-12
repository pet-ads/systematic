package br.all.report.controller

import br.all.application.question.repository.QuestionRepository
import br.all.application.report.find.service.FindAnswersServiceImpl
import br.all.application.study.repository.StudyReviewRepository
import br.all.application.user.CredentialsService
import br.all.domain.services.IdGeneratorService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ReportControllerConfiguration {

    @Bean
    fun findAnswersService(
        questionRepository: QuestionRepository,
        studyReviewRepository: StudyReviewRepository,
        credentialsService: CredentialsService,
        idGenerator: IdGeneratorService
    ) = FindAnswersServiceImpl(
        questionRepository,
        studyReviewRepository,
        credentialsService,
        idGenerator
    )
}