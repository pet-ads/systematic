package br.all.application.review.repository

import br.all.domain.model.review.SystematicStudy

fun SystematicStudy.toDto() = SystematicStudyDto(
    id.value,
    title,
    description,
    researchers.map { it.value }
        .toSet(),
)
