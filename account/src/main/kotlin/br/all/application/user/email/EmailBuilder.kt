package br.all.application.user.email

import br.all.application.user.email.template.ConfirmAccountTemplate
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class EmailBuilder {
    fun buildConfirmAccount(
        email: String,
        id: UUID,
        country: String = "pt-BR"
    ): EmailMessage {

        val link = "http://localhost:5173/#/confirm-account?id=$id"

        val subject = ConfirmAccountTemplate.subject(country)

        val body = ConfirmAccountTemplate.body(country, link)

        return EmailMessage(
            to = email,
            subject = subject,
            body = body,
            isHtml = false
        )
    }
}