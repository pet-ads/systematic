package br.all.application.review.create

import java.util.*

data class SystematicStudyRequestModel(
    val title : String,
    val description : String,
    val owner : UUID,
    val collaborators : Set<UUID>,
)
