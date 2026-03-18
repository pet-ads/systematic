package br.all.domain.shared.email.domain

data class EmailMessage(
    val to: String,
    val subject: String,
    val body: String,
    val isHtml: Boolean
)
