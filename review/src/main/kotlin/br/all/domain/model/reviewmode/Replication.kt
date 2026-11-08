package br.all.domain.model.reviewmode

import br.all.domain.shared.user.ResearcherId
import kotlin.math.roundToInt

class Replication(
    val mainReviewer: ResearcherId,
    val secondaryReviewers: Map<ResearcherId, Double> = emptyMap(),
    val noStudySharedBySecondaries: Boolean = true,
    override val acceptMajority: Boolean = false,
) : ReviewMode {
    val reviewers: Set<ResearcherId> = setOf(mainReviewer) + secondaryReviewers.keys

    override fun assign(
        availableStudies: List<Long>,
        reviewerStudies: Map<ResearcherId, Set<Long>>
    ): Map<ResearcherId, Set<Long>> {
        require(availableStudies.isNotEmpty()) { "There must be at least one available study to split" }
        val result = mutableMapOf(mainReviewer to availableStudies.toSet())

        var cursor = 0
        secondaryReviewers.forEach { (reviewer, percentage) ->
            val quota = (availableStudies.size * percentage / 100).roundToInt()
            val picked = if (noStudySharedBySecondaries) {
                availableStudies.drop(cursor).take(quota).also { cursor += it.size }
            } else {
                availableStudies.take(quota)
            }
            result[reviewer] = picked.toSet()
        }

        return result
    }

    init {
        require(secondaryReviewers.values.all { it in 0.0..100.0 }) {
            "Each secondary percentage must be between 0 and 100"
        }

        if (noStudySharedBySecondaries) {
            require(secondaryReviewers.values.sum() <= 100.0) {
                "Secondary percentages cannot exceed 100 when studies are not shared"
            }
        }
    }
}
