package br.ifsp.octaviano

import br.all.domain.services.SelectionStatusSuggestionService
import br.all.domain.services.SelectionStatusSuggestionService.RequestModel

class Principal {
    fun main(args: Array<String>) {
        val input = listOf(
            SelectionStatusSuggestionService.StudyReviewInfo(1, 231, 3, 2023),
        )

        val service: SelectionStatusSuggestionService = ScasImpl()
        val responseModel = service.buildSuggestions(RequestModel(input))

        responseModel.studySuggestions.forEach { println(it) }
    }
}