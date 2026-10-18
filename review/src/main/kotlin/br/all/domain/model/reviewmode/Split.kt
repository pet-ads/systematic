package br.all.domain.model.reviewmode

import br.all.domain.shared.user.ResearcherId

class Split(
    val reviewers: List<ResearcherId> = emptyList(),
    val reviewPerStudy: Int,
) : ReviewMode {
    constructor(reviewPerStudy: Int) : this(emptyList(), reviewPerStudy)

    init {
        require(reviewers.isNotEmpty()) { "Split needs at least one reviewer" }
        require(reviewPerStudy in 1..reviewers.size) {
            "Review per study must be between 1 and the number of reviewers"
        }
    }

    fun split(
        availableStudies: List<Long>,
        reviewerStudies: Map<ResearcherId, Set<Long>>,
    ): Map<ResearcherId, Set<Long>> {
        require(availableStudies.isNotEmpty()) { "There must be at least one available study to split" }
        require(reviewerStudies.keys.all { it in reviewers }) {
            "Reviewer studies can only contain researchers configured in the split"
        }
        require(availableStudies.none { study -> reviewerStudies.values.count { study in it } >= reviewPerStudy }) {
            "Available studies cannot already be assigned to the maximum number of reviewers"
        }

        val load = reviewers.associateWith { reviewerStudies[it]?.size ?: 0 }.toMutableMap()
        val result = reviewers.associateWith { mutableSetOf<Long>() }

        availableStudies.forEach { study ->
            reviewers
                .filter { study !in (reviewerStudies[it] ?: emptySet()) }
                .sortedBy { load.getValue(it) }
                .take(reviewPerStudy)
                .forEach { reviewer ->
                    result.getValue(reviewer).add(study)
                    load[reviewer] = load.getValue(reviewer) + 1
                }
        }
        return result
    }
}
