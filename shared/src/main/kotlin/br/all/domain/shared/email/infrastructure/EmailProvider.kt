package br.all.domain.shared.email.infrastructure

import br.all.domain.shared.email.domain.EmailMessage

interface EmailProvider {
    fun send(email: EmailMessage)
}