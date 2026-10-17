package br.all.agreement.controller

import br.all.application.agreement.repository.AgreementVisibilityRepository
import br.all.application.agreement.visibility.GetAgreementVisibilityServiceImpl
import br.all.application.agreement.visibility.SetAgreementVisibilityServiceImpl
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.user.CredentialsService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AgreementServicesConfiguration {

    @Bean
    fun setAgreementVisibilityService(
        systematicStudyRepository: SystematicStudyRepository,
        agreementVisibilityRepository: AgreementVisibilityRepository,
        credentialsService: CredentialsService,
    ) = SetAgreementVisibilityServiceImpl(
        systematicStudyRepository, agreementVisibilityRepository, credentialsService
    )

    @Bean
    fun getAgreementVisibilityService(
        systematicStudyRepository: SystematicStudyRepository,
        agreementVisibilityRepository: AgreementVisibilityRepository,
        credentialsService: CredentialsService,
    ) = GetAgreementVisibilityServiceImpl(
        systematicStudyRepository, agreementVisibilityRepository, credentialsService
    )
}