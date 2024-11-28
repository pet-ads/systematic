package br.all.domain.services


import br.all.domain.model.review.SystematicStudyId
import br.all.domain.model.search.SearchSessionID
import br.all.domain.model.study.StudyReview

class ConverterFactoryService(
    private val bibtexConverterService: BibtexConverterService,
    private val risConverterService: RisConverterService
) {
    fun extractReferences(
        systematicStudyId: SystematicStudyId,
        searchSessionId: SearchSessionID,
        file: String
    ): Pair<List<StudyReview>, List<String>> {
        val bibtexPattern = Regex("""@\s*(article|book|conference|inproceedings|techreport|phdthesis|mastersthesis)\s*""", RegexOption.IGNORE_CASE)
        val risPattern = Regex("""^TY\s+-\s+""", RegexOption.MULTILINE)

        return when {
            bibtexPattern.containsMatchIn(file) -> {
                bibtexConverterService.convertManyToStudyReview(systematicStudyId, searchSessionId, file)
            }
            risPattern.containsMatchIn(file) -> {
                risConverterService.convertManyToStudyReview(systematicStudyId, searchSessionId, file)
            }
            else -> {
                Pair(emptyList(), listOf("File format not recognized or unsupported"))
            }
        }
    }
}
