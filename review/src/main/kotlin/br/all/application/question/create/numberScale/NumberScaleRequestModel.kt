package br.all.application.question.create.numberScale

data class NumberScaleRequestModel(
    val code: String,
    val description: String,
    val higher: Int,
    val lower: Int
)
