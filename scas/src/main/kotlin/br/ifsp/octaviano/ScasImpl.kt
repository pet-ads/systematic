package br.ifsp.octaviano

import br.all.domain.services.SelectionStatusSuggestionService
import br.all.domain.services.SelectionStatusSuggestionService.*
import java.math.RoundingMode

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
                ((1 + it.studyReviewCitations) * (1 - index * (mostRecentYear - it.studyReviewYear))).toBigDecimal().setScale(2, RoundingMode.HALF_EVEN).toDouble()
        }
    }

    private fun normalizeScores(studiesInfo: List<StudyInfo>) {
        val scores = studiesInfo.map { it.studyReviewScore }
        val max = scores.max().toDouble()
        studiesInfo.forEach { it.studyReviewNormalizedScore = (it.studyReviewScore / max).toBigDecimal().setScale(2, RoundingMode.HALF_EVEN).toDouble()
        }
    }

    private fun normalizeCitationCoefficients(studiesInfo: List<StudyInfo>) {
        val citationCoefficients = studiesInfo.mapNotNull { it.studyReviewCitationCoefficient }
        val max = citationCoefficients.max()
        studiesInfo.forEach {
            it.studyReviewNormalizedCitationCoefficient = (it.studyReviewCitationCoefficient?.div(max))?.toBigDecimal()?.setScale(2, RoundingMode.HALF_EVEN)?.toDouble()
        }
    }

    private fun classifyNormalizedScores(studiesInfo: List<StudyInfo>) {
        studiesInfo.forEach {
            it.studyReviewNormalizedScore?.let { studyReviewNormalizedScore -> run {
                it.studyScoreClassification = when {
                    studyReviewNormalizedScore >= 0.46 -> HazyValues.HIGH
                    studyReviewNormalizedScore >= 0.13 -> HazyValues.MEDIUM
                    else -> HazyValues.LOW
                }
            }
        }}
    }

    private fun classifyNormalizedCitationCoefficients(studiesInfo: List<StudyInfo>) {
        studiesInfo.forEach {
            it.studyReviewNormalizedCitationCoefficient?.let { studyReviewNormalizedCitationCoefficient -> run {
                it.studyCitationCoefficientClassification = when {
                    studyReviewNormalizedCitationCoefficient >= 0.395 -> HazyValues.HIGH
                    studyReviewNormalizedCitationCoefficient >= 0.12 -> HazyValues.MEDIUM
                    else -> HazyValues.LOW
                }
            }
        }}
    }

    private fun suggestStudiesClassification(studiesInfo: List<StudyInfo>): List<StudyStatusSuggestion> {
        studiesInfo.forEach {
            it.studyReviewSuggestion = when {
                (it.studyScoreClassification == HazyValues.HIGH
                        && (it.studyCitationCoefficientClassification == HazyValues.HIGH
                            || it.studyCitationCoefficientClassification == HazyValues.MEDIUM)
                ) || (it.studyScoreClassification == HazyValues.MEDIUM
                        && it.studyCitationCoefficientClassification == HazyValues.HIGH)
                -> "Automatic inclusion"

                it.studyScoreClassification == HazyValues.LOW
                        && (it.studyCitationCoefficientClassification == HazyValues.MEDIUM
                            || it.studyCitationCoefficientClassification == HazyValues.LOW)
                -> "Automatic exclusion"

                else -> "Manual review"
            }
        }

        return studiesInfo.mapNotNull { it.studyReviewSuggestion?.let { studyReviewSuggestion ->
            StudyStatusSuggestion(it.studyReviewId,
                studyReviewSuggestion
            )
        } }
    }
}
