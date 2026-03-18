package br.all.domain.shared.email.infrastructure

import br.all.domain.shared.email.domain.EmailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper

class SmtpEmailProvider (
    private val mailSender: JavaMailSender,
): EmailProvider {
    override fun send(email: EmailMessage) {
        val mimeMessage = mailSender.createMimeMessage()
        val helper = MimeMessageHelper(mimeMessage, true)

        helper.setTo(email.to)
        helper.setSubject(email.subject)
        helper.setText(email.body, email.isHtml)

        mailSender.send(mimeMessage)
    }
}