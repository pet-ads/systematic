package br.all.application.user.email

import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service

@Service
class  EmailServiceImpl(
    private val emailProvider: EmailProvider
): EmailService {
    @Async("emailExecutor")
    override fun sendAsync(message: EmailMessage) {
        try {
            emailProvider.send(message)
        } catch (ex: Exception) {
            println("Erro ao enviar email: ${ex.message}")
        }
    }
}