package br.all.domain.services


import br.all.domain.model.review.SystematicStudyId
import br.all.domain.model.study.StudyReview
import java.util.*


class ConverterFactoryService(
    private val bibtexConverterService: BibtexConverterService,
    private val risConverterService: RisConverterService
){
    // , request: RequestModel
    fun extractReferences(file: String): List<StudyReview> {
        var studyReviews = listOf<StudyReview>()

        val bibtexPattern = Regex("""@\s*(article|book|conference|inproceedings|techreport|phdthesis|mastersthesis)\s*""", RegexOption.IGNORE_CASE)

        val risPattern = Regex("""^TY\s+-\s+""", RegexOption.MULTILINE)

        if (bibtexPattern.containsMatchIn(file)) {
            studyReviews = bibtexConverterService.convertManyToStudyReview(SystematicStudyId(UUID.randomUUID()), file)
        } else if (risPattern.containsMatchIn(file)) {
            studyReviews = risConverterService.convertManyToStudyReview(SystematicStudyId(UUID.randomUUID()), file)
        }

        return studyReviews
    }

}