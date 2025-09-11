package br.all.user.controller

import br.all.application.user.create.RegisterUserAccountServiceImpl
import br.all.application.user.find.RetrieveUserProfileServiceImpl
import br.all.application.user.repository.UserAccountRepository
import br.all.application.user.update.PatchUserProfileServiceImpl
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class UserAccountConfiguration {

    @Bean
    fun registerUser(repository: UserAccountRepository) = RegisterUserAccountServiceImpl(repository)

    @Bean
    fun retrieveUserProfile(repository: UserAccountRepository) = RetrieveUserProfileServiceImpl(repository)

    @Bean
    fun patchUserProfile(repository: UserAccountRepository) = PatchUserProfileServiceImpl(repository)
}