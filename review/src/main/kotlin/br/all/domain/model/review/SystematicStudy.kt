package br.all.domain.model.review

import br.all.domain.model.researcher.ResearcherId
import br.all.domain.shared.ddd.Entity
import br.all.domain.shared.utils.requireThatExists

class SystematicStudy(
    val reviewId: ReviewId,
    title: String,
    description: String,
    owner: ResearcherId,
    collaborators: MutableSet<ResearcherId> = mutableSetOf(),
) : Entity(reviewId) {

    private val _collaborators = collaborators
    val collaborators get() = _collaborators.toSet()
    var owner = owner
        private set
    var title: String = ""
        set(value) {
            require(value.isNotBlank()) { "Title must not be blank." }
            field = value
        }
    var description = ""
        set(value) {
            require(value.isNotBlank()) { "Description must not be blank." }
            field = value
        }

    init {
        this.title = title
        this.description = description
        collaborators.add(owner)
    }

    companion object

    fun addCollaborator(researcherId: ResearcherId) = _collaborators.add(researcherId)

    fun changeOwner(researcherId: ResearcherId){
        if (researcherId !in _collaborators) _collaborators.add(researcherId)
        owner = researcherId
    }

    fun removeCollaborator(researcherId: ResearcherId) {
        check(researcherId != owner) { "Cannot remove the Systematic Study owner: $owner" }
        requireThatExists(researcherId in _collaborators)
            { "Cannot remove member that is not part of the collaboration: $researcherId" }
        _collaborators.remove(researcherId)
    }


    override fun toString() = "SystematicStudy(reviewId=$reviewId, title='$title', " +
            "description='$description', owner=$owner, researchers=$_collaborators)"

}