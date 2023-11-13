package br.all.application.protocol.repository

data class PICOCDto(
    val population: String,
    val intervention: String,
    val control: String,
    val outcome: String,
    val context: String?,
)
