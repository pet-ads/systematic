package br.all.application.user.email

import org.springframework.stereotype.Service

@Service
class  EmailService(
    private val emailProvider: EmailProvider
) {
    fun send(email: EmailMessage) {
        emailProvider.send(email)
    }
}