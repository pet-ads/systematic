package br.all.application.review.create

import java.util.*

data class SystematicStudyRequestModel(
    val title : String,
    val description : String,
    val researchers : Set<UUID>,
)
