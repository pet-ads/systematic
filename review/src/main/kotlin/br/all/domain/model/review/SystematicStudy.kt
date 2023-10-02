package br.all.domain.model.review

import br.all.domain.model.researcher.ResearcherId
import br.all.domain.shared.ddd.Entity
import br.all.domain.shared.ddd.Notification

class SystematicStudy(
        val reviewId: ReviewId,
        val title: String,
        val description: String,
        var owner: ResearcherId,
        val collaborators: MutableSet<ResearcherId> = mutableSetOf(),
) : Entity(reviewId) {

    init {
        val notification = validate()
        require(notification.hasNoErrors()) { notification.message() }
        collaborators.add(owner)
    }

    private fun validate() : Notification {
        val notification = Notification()
        if(title.isBlank())
            notification.addError("Title must not be blank")
        if(description.isBlank())
            notification.addError("Description must not be blank")
        return notification
    }

    //TODO: add all unit tests
    companion object

    fun changeOwner(researcherId: ResearcherId){
        if (!containsCollaborator(researcherId))
            collaborators.add(researcherId)
        owner = researcherId
    }

    fun addCollaborator(researcherId: ResearcherId) = collaborators.add(researcherId)

    fun removeCollaborator(researcherId: ResearcherId) {
        if (researcherId == owner)
            throw IllegalStateException("Can not remove the Systematic Study owner: $owner")
        if (!containsCollaborator(researcherId))
            throw NoSuchElementException("Can not remove member that is not part of the collaboration")
        collaborators.remove(researcherId)
    }

    fun containsCollaborator(researcherId: ResearcherId) = collaborators.contains(researcherId)

    override fun toString() = "SystematicStudy(reviewId=$reviewId, title='$title', description='$description', " +
            "owner=$owner, collaborators=$collaborators)"
}