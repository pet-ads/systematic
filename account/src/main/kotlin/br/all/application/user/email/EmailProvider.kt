package br.all.application.user.email

interface EmailProvider {
    fun send(email: EmailMessage)
}