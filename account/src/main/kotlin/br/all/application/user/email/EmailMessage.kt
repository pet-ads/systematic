package br.all.application.user.email

class EmailMessage(
    val to: String,
    val subject: String,
    val body: String,
    val isHtml: Boolean
)