package br.all.domain.shared.email.application

import br.all.domain.shared.email.domain.EmailMessage
import br.all.domain.shared.email.infrastructure.EmailProvider
import org.springframework.stereotype.Service

@Service
class EmailService(
    private val emailProvider: EmailProvider
) {
    fun send(email: EmailMessage) {
        emailProvider.send(email)
    }
}