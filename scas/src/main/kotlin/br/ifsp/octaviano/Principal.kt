package br.ifsp.octaviano

import br.all.domain.services.SelectionStatusSuggestionService
import br.all.domain.services.SelectionStatusSuggestionService.*

fun main() {
    val input = listOf(
        StudyReviewInfo(1, 21, 10, 2023),
        StudyReviewInfo(2, 0, 0, 2006),
        StudyReviewInfo(3, 54, 40, 2023),
        StudyReviewInfo(4, 34, 30, 2015),
    )

    val service: SelectionStatusSuggestionService = ScasImpl()
    val responseModel = service.buildSuggestions(RequestModel(input))

    responseModel.studySuggestions.forEach { println(it) }
}
