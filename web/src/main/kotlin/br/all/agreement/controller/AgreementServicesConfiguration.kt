package br.all.agreement.controller

import br.all.application.agreement.repository.AgreementVisibilityRepository
import br.all.application.agreement.visibility.GetAgreementVisibilityServiceImpl
import br.all.application.agreement.visibility.SetAgreementVisibilityServiceImpl
import br.all.application.shared.service.AuthorizationService
import br.all.application.study.agreement.AgreementStudyReviewService
import br.all.application.study.update.implementation.UpdateStudyReviewExtractionService
import br.all.application.study.update.implementation.UpdateStudyReviewPriorityService
import br.all.application.study.update.implementation.UpdateStudyReviewSelectionService
import br.all.application.study.update.interfaces.BatchAnswerQuestionService
import br.all.application.study.update.interfaces.RemoveCriteriaService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AgreementServicesConfiguration {

    @Bean
    fun setAgreementVisibilityService(
        authorizationService: AuthorizationService,
        agreementVisibilityRepository: AgreementVisibilityRepository,
    ) = SetAgreementVisibilityServiceImpl(authorizationService, agreementVisibilityRepository)

    @Bean
    fun getAgreementVisibilityService(
        authorizationService: AuthorizationService,
        agreementVisibilityRepository: AgreementVisibilityRepository,
    ) = GetAgreementVisibilityServiceImpl(authorizationService, agreementVisibilityRepository)

    @Bean
    fun agreementStudyReviewService(
        authorizationService: AuthorizationService,
        updateReviewSelectionService: UpdateStudyReviewSelectionService,
        updateReviewExtractionService: UpdateStudyReviewExtractionService,
        updateReviewPriorityService: UpdateStudyReviewPriorityService,
        batchAnswerQuestionService: BatchAnswerQuestionService,
        removeCriteriaService: RemoveCriteriaService,
    ) = AgreementStudyReviewService(
        authorizationService,
        updateReviewSelectionService,
        updateReviewExtractionService,
        updateReviewPriorityService,
        batchAnswerQuestionService,
        removeCriteriaService,
    )
}