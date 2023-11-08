package br.all.application.protocol.question.create.numberScale

data class NumberScaleRequestModel(
    val code: String,
    val description: String,
    val higher: Int,
    val lower: Int
)
