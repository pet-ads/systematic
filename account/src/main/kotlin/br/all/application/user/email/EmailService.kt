package br.all.application.user.email

interface EmailService {
    fun sendAsync(message: EmailMessage)
}