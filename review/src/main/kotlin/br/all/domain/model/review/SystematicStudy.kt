package br.all.domain.model.review

import br.all.domain.model.researcher.ResearcherId
import br.all.domain.shared.ddd.Entity

class SystematicStudy(
    val id : ReviewId,
    val title : String,
    val description : String,
    var owner : ResearcherId,
    val collaborators : MutableSet<ResearcherId>,
) : Entity(id) {

    //TODO: add all unit tests
    companion object

    fun addResearcher(researcherId : ResearcherId) = collaborators.add(researcherId)

    fun removeResearcher(researcherId: ResearcherId) {
        require(researcherId != owner) {"Can not remove the Systematic Study owner: $owner"}
        collaborators.remove(researcherId)
    }

    override fun toString(): String {
        return "SystematicStudy(id=$id, title='$title', description='$description', researchers=$collaborators)"
    }
}