package br.all.application.user.email.template

object InviteCollaboratorTemplate {

    fun subject(country: String, title: String) = when (country) {
        "Brazil", "Portugal" -> "Convite para colaborar em revisão sistemática - $title"
        else -> "Invitation to collaborate on systematic review - $title"
    }

    fun body(country: String, name: String, inviter: String, title: String, link: String) = when (country) {
        "Brazil", "Portugal" -> """
            Olá, $name!
            
            $inviter convidou você para colaborar na revisão sistemática:
            
            $title
            
            Clique no link abaixo para responder ao convite:
            $link
            """.trimIndent()

        else -> """
            Hello, $name!

            $inviter invited you to collaborate on the following systematic review:
            
            $title
            
            Click the link below to respond to the invitation:
            $link
        """.trimIndent()
    }
}