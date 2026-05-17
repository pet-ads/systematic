package br.all.application.user.email

import br.all.application.user.email.template.ConfirmAccountTemplate
import br.all.application.user.email.template.InviteCollaboratorTemplate
import br.all.application.user.email.template.PasswordRecoveryTemplate
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class EmailBuilder {
    fun buildConfirmAccount(
        email: String,
        id: UUID,
        country: String
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
        
        
    fun buildPasswordRecovery(
        email: String,
        name: String,
        token: UUID,
        country: String = "pt-BR"
    ): EmailMessage {

        val link = "http://localhost:5173/#/reset-password?token=$token"

        val subject = PasswordRecoveryTemplate.subject(country)

        val body = PasswordRecoveryTemplate.body(country, name, link)

        return EmailMessage(
            to = email,
            subject = subject,
            body = body,
            isHtml = false
        )
    }

    fun buildInviteCollaborator(
        email: String,
        name: String,
        title: String,
        inviter: String,
        token: UUID,
        country: String = "pt-BR"
    ): EmailMessage {

        val link = "http://localhost:5173/#/join-review?token=$token"

        val subject = InviteCollaboratorTemplate.subject(country, title)

        val body = InviteCollaboratorTemplate.body(country, name, inviter, title, link)

        return EmailMessage(
            to = email,
            subject = subject,
            body = body,
            isHtml = false
        )
    }
}