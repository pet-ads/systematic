package br.ifsp.octaviano

import br.all.domain.services.SelectionStatusSuggestionService
import br.all.domain.services.SelectionStatusSuggestionService.*

class ScasImpl : SelectionStatusSuggestionService {
    private enum class HazyValues {
        LOW, MEDIUM, HIGH
    }

    private data class StudyInfo(
        val studyReviewId: Long,
        val studyReviewScore: Int,
        val studyReviewCitations: Int,
        val studyReviewYear: Int,
        var studyReviewNormalizedScore: Double? = null,
        var studyScoreClassification: HazyValues? = null,
        var studyReviewCitationCoefficient: Double? = null,
        var studyReviewNormalizedCitationCoefficient: Double? = null,
        var studyCitationCoefficientClassification: HazyValues? = null,
        var studyReviewSuggestion: String? = null,
    )

    override fun buildSuggestions(request: RequestModel): ResponseModel {
        val studiesInfo = convertToCompleteStudyInfo(request.studiesInfo)

        val mostRecentYear = getMostRecentYear(studiesInfo)

        calculateCitationCoefficients(studiesInfo, mostRecentYear)
        normalizeScores(studiesInfo)
        normalizeCitationCoefficients(studiesInfo)
        classifyNormalizedScores(studiesInfo)
        classifyNormalizedCitationCoefficients(studiesInfo)
        val studySuggestions = suggestStudiesClassification(studiesInfo)

        return ResponseModel(studySuggestions)
    }

    private fun convertToCompleteStudyInfo(studiesInfo: List<StudyReviewInfo>): List<StudyInfo> {
        return studiesInfo.map {
            StudyInfo(
                it.studyReviewId, it.studyReviewScore, it.studyReviewCitations, it.studyReviewYear
            )
        }
    }

    private fun getMostRecentYear(studiesInfo: List<StudyInfo>): Int {
        val years = studiesInfo.map { it.studyReviewYear }
        return years.max()
    }

    private fun calculateCitationCoefficients(
        studiesInfo: List<StudyInfo>, mostRecentYear: Int, index: Double = 0.05
    ) {
        studiesInfo.forEach {
            it.studyReviewCitationCoefficient =
                (1 + it.studyReviewCitations) * (1 - index * (mostRecentYear - it.studyReviewYear))
        }
    }

    private fun normalizeScores(studiesInfo: List<StudyInfo>) {
        val scores = studiesInfo.map { it.studyReviewScore }
        val max = scores.max()
        studiesInfo.forEach { it.studyReviewNormalizedScore = it.studyReviewScore.toDouble() / max }
    }

    private fun normalizeCitationCoefficients(studiesInfo: List<StudyInfo>) {
        val citationCoefficients = studiesInfo.map { it.studyReviewCitationCoefficient!! }
        val max = citationCoefficients.max()
        studiesInfo.forEach {
            it.studyReviewNormalizedCitationCoefficient = it.studyReviewCitationCoefficient?.div(max)
        }
    }

    private fun classifyNormalizedScores(studiesInfo: List<StudyInfo>) {
        studiesInfo.forEach {
            it.studyScoreClassification = when (it.studyReviewNormalizedScore!!) {
                in 0.0..0.14 -> HazyValues.LOW
                in 0.15..0.45 -> HazyValues.MEDIUM
                else -> HazyValues.HIGH
            }
        }
    }

    private fun classifyNormalizedCitationCoefficients(studiesInfo: List<StudyInfo>) {
        studiesInfo.forEach {
            it.studyCitationCoefficientClassification = when (it.studyReviewNormalizedCitationCoefficient!!) {
                in 0.0..0.1 -> HazyValues.LOW
                in 0.11..0.19 -> HazyValues.MEDIUM
                else -> HazyValues.HIGH
            }
        }
    }

    private fun suggestStudiesClassification(studiesInfo: List<StudyInfo>): List<StudyStatusSuggestion> {
        studiesInfo.forEach {
            it.studyReviewSuggestion = when {
                it.studyScoreClassification == HazyValues.HIGH
                        && it.studyCitationCoefficientClassification == HazyValues.HIGH
                        || it.studyCitationCoefficientClassification == HazyValues.MEDIUM
                -> "Automatic inclusion"

                it.studyScoreClassification == HazyValues.LOW
                        && it.studyCitationCoefficientClassification == HazyValues.MEDIUM
                        || it.studyCitationCoefficientClassification == HazyValues.LOW
                -> "Automatic exclusion"

                else -> "Manual review"
            }
        }
        return studiesInfo.map { StudyStatusSuggestion(it.studyReviewId, it.studyReviewSuggestion!!) }
    }
}
