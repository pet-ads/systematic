package br.all.user.controller

import br.all.application.user.create.RegisterUserAccountServiceImpl
import br.all.application.user.repository.UserAccountRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class UserAccountConfiguration {

    @Bean
    fun registerUser(repository: UserAccountRepository) = RegisterUserAccountServiceImpl(repository)
}