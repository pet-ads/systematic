package br.all.application.review.repository

import java.util.*

data class SystematicStudyDto(
    val id : UUID,
    val title : String,
    val description : String,
    val owner : UUID,
    var collaborators : Set<UUID>,
    val objectives: String,
)
