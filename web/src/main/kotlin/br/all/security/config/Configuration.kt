package br.all.security.config

import br.all.application.user.find.LoadCredentialsByTokenService
import br.all.application.user.find.LoadCredentialsByTokenServiceImpl
import br.all.application.user.find.LoadAccountCredentialsService
import br.all.application.user.find.LoadAccountCredentialsServiceImpl
import br.all.application.user.repository.UserAccountRepository
import br.all.application.user.update.UpdateRefreshTokenService
import br.all.application.user.update.UpdateRefreshTokenServiceImpl
import br.all.security.service.CustomUserDetailService
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder

@Configuration
@EnableConfigurationProperties(JwtProperties::class)
class Configuration {

    @Bean
    fun loadAccountCredentialService(repository: UserAccountRepository): LoadAccountCredentialsService =
        LoadAccountCredentialsServiceImpl(repository)

    @Bean
    fun userDetailsService(repository: UserAccountRepository): UserDetailsService =
        CustomUserDetailService(loadAccountCredentialService(repository))

    @Bean
    fun updateToken(repository: UserAccountRepository): UpdateRefreshTokenService =
        UpdateRefreshTokenServiceImpl(repository)

    @Bean
    fun loadUserDetailsByToken(repository: UserAccountRepository): LoadCredentialsByTokenService =
        LoadCredentialsByTokenServiceImpl(repository)

    @Bean
    fun encoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun authenticatorProvider(repository: UserAccountRepository): AuthenticationProvider =
        DaoAuthenticationProvider().also {
            it.setPasswordEncoder(encoder())
            it.setUserDetailsService(userDetailsService(repository))
        }

    @Bean
    fun authenticationManager(config: AuthenticationConfiguration): AuthenticationManager =
        config.authenticationManager
}