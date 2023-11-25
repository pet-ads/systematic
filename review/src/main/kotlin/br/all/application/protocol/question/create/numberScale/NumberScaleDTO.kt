package br.all.application.protocol.question.create.numberScale

import java.util.*

data class NumberScaleDTO (
    val questionId: UUID,
    val protocolId: UUID,
    val code: String,
    val description: String,
    val higher: Int,
    val lower: Int
    )