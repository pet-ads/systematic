package br.all.domain.model.review

import br.all.domain.model.researcher.ResearcherId
import br.all.domain.shared.ddd.Entity

// todo: replace type title and description properties from String to Text when it is implemented
class SystematicStudy(
    val id : ReviewId,
    val title : String,
    val description : String,
    val researchers : MutableSet<ResearcherId>,
) : Entity(id) {
    init {
        require(researchers.isNotEmpty()) { "There must be at least one researcher working on this systematic study!" }
    }

    fun addResearcher(researcherId : ResearcherId) = researchers.add(researcherId)

    fun removeResearcher(researcherId: ResearcherId) {
        if (researchers.size == 1)
            throw IllegalStateException("Unable to remove the last researcher! There must be at least one researcher" +
                    " working on this systematic study!")
        researchers.remove(researcherId)
    }

    override fun toString(): String {
        return "SystematicStudy(id=$id, title='$title', description='$description', researchers=$researchers)"
    }
}