package br.all.application.user.email.template

object ConfirmAccountTemplate {

    fun subject(country: String) = when (country) {
        "Brazil", "Portugal" -> "Recuperação de senha"
        else -> "Password Recovery"
    }

    fun body(country: String, link: String) = when (country) {
        "Brazil", "Portugal" -> """
            Olá,

            Obrigado por se cadastrar!
            
            Para ativar sua conta, basta clicar no link abaixo:
            
            $link
            
            Se você não realizou esse cadastro, pode ignorar este e-mail.
        """.trimIndent()

        else -> """
            Hello,

            Thank you for signing up!
            
            To activate your account, please click the link below:
            
            $link
            
            If you did not create this account, you can safely ignore this email.
        """.trimIndent()
    }
}