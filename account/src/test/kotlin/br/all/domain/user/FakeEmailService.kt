package br.all.domain.user

import br.all.application.user.email.EmailMessage
import br.all.application.user.email.EmailService

object FakeEmailService: EmailService {
    override fun sendAsync(message: EmailMessage) {

    }
}