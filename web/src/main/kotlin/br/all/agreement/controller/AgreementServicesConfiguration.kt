package br.all.agreement.controller

import br.all.application.agreement.repository.AgreementVisibilityRepository
import br.all.application.agreement.visibility.GetAgreementVisibilityServiceImpl
import br.all.application.agreement.visibility.SetAgreementVisibilityServiceImpl
import br.all.application.shared.service.AuthorizationService
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
}