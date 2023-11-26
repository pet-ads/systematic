package br.all.application.question.create.pickList

data class PickListRequestModel(
    val code: String,
    val description: String,
    val options: List<String>
)