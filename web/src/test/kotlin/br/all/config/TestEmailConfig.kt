package br.all.config

import br.all.application.user.email.EmailMessage
import br.all.application.user.email.EmailProvider
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean

@TestConfiguration
class TestEmailConfig {

    @Bean
    fun emailProvider(): EmailProvider {
        return object : EmailProvider {
            override fun send(email: EmailMessage) {
            }
        }
    }
}