package br.all.application.user.email

import br.all.application.user.email.template.PasswordRecoveryTemplate
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class EmailBuilder {
    fun buildPasswordRecovery(
        email: String,
        name: String,
        token: UUID,
        country: String = "pt-BR"
    ): EmailMessage {

        val link = "http://localhost:8080/reset-password?token=$token"

        val subject = PasswordRecoveryTemplate.subject(country)

        val body = PasswordRecoveryTemplate.body(country, name, link)

        return EmailMessage(
            to = email,
            subject = subject,
            body = body,
            isHtml = false
        )
    }
}