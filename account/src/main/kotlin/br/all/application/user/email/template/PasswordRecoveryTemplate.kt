package br.all.application.user.email.template

object PasswordRecoveryTemplate {

    fun subject(country: String) = when (country) {
        "Brazil", "Portugal" -> "Recuperação de senha"
        else -> "Password Recovery"
    }

    fun body(country: String, name: String, link: String) = when (country) {
        "Brazil", "Portugal" -> """
            Olá, $name!

            Recebemos uma solicitação para redefinir sua senha.

            Clique no link abaixo:
            $link

            Se não foi você, ignore este email.
        """.trimIndent()

        else -> """
            Hello, $name!

            We received a request to reset your password.

            Click the link below:
            $link

            If it wasn't you, ignore this email.
        """.trimIndent()
    }
}