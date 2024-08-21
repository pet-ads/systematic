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
        if (file.contains("@")) {
            studyReviews = bibtexConverterService.convertManyToStudyReview(SystematicStudyId(UUID.randomUUID()), file)
        }
        else if (file.contains("TY")) {
            studyReviews = risConverterService.convertManyToStudyReview(SystematicStudyId(UUID.randomUUID()), file)
        }
        return studyReviews
    }
}