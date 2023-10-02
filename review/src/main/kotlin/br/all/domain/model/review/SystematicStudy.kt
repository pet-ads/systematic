package br.all.domain.model.review

import br.all.domain.model.researcher.ResearcherId
import br.all.domain.shared.ddd.Entity
import br.all.domain.shared.ddd.Notification

class SystematicStudy(
        val reviewId: ReviewId,
        val title: String,
        val description: String,
        var owner: ResearcherId,
        collaborators: MutableSet<ResearcherId> = mutableSetOf(),
) : Entity(reviewId) {
    private val _collaborators = collaborators
    val collaborators get() = _collaborators.toSet()

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

    companion object

    fun changeOwner(researcherId: ResearcherId){
        if (!containsCollaborator(researcherId))
            _collaborators.add(researcherId)
        owner = researcherId
    }

    fun addCollaborator(researcherId: ResearcherId) = _collaborators.add(researcherId)

    fun removeCollaborator(researcherId: ResearcherId) {
        if (researcherId == owner)
            throw IllegalStateException("Cannot remove the Systematic Study owner: $owner")
        if (!containsCollaborator(researcherId))
            throw NoSuchElementException("Cannot remove member that is not part of the collaboration")
        _collaborators.remove(researcherId)
    }

    fun containsCollaborator(researcherId: ResearcherId) = _collaborators.contains(researcherId)

    override fun toString() = "SystematicStudy(reviewId=$reviewId, title='$title', description='$description', " +
            "owner=$owner, _collaborators=$_collaborators)"
}