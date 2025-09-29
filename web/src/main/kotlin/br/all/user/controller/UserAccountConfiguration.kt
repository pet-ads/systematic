package br.all.user.controller

import br.all.application.user.create.RegisterUserAccountServiceImpl
import br.all.application.user.find.RetrieveUserProfileServiceImpl
import br.all.application.user.repository.UserAccountRepository
import br.all.application.user.update.ChangeAccountPasswordServiceImpl
import br.all.application.user.update.PatchUserProfileServiceImpl
import br.all.domain.shared.service.PasswordEncoderPort
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class UserAccountConfiguration {

    @Bean
    fun registerUser(repository: UserAccountRepository, encoder: PasswordEncoderPort) = RegisterUserAccountServiceImpl(repository, encoder)

    @Bean
    fun retrieveUserProfile(repository: UserAccountRepository) = RetrieveUserProfileServiceImpl(repository)

    @Bean
    fun patchUserProfile(repository: UserAccountRepository) = PatchUserProfileServiceImpl(repository)

    @Bean
    fun changeAccountPassword(repository: UserAccountRepository, encoder: PasswordEncoderPort) = ChangeAccountPasswordServiceImpl(repository, encoder)
}