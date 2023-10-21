package br.all.domain.model.review

import br.all.domain.model.researcher.ResearcherId
import br.all.domain.shared.ddd.Entity
import br.all.domain.shared.ddd.Notification
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

    //TODO I changed trying to keep it symple
    var title: String = title
        set(value) {
            require(value.isNotBlank()) { "Title must not be blank." }
            field = value
        }

    var description = description
        set(value){
            require(value.isNotBlank()) { "Description must not be blank." }
            field = value
        }

    init {
        collaborators.add(owner)
    }

    companion object

    fun addCollaborator(researcherId: ResearcherId) = _collaborators.add(researcherId)

    fun changeOwner(researcherId: ResearcherId){
        if (!_collaborators.contains(researcherId))
            _collaborators.add(researcherId)
        owner = researcherId
    }

    fun removeCollaborator(researcherId: ResearcherId) {
        if (researcherId == owner)
            throw IllegalStateException("Cannot remove the Systematic Study owner: $owner")

        if(!_collaborators.contains(researcherId))
            throw NoSuchElementException("Cannot remove member that is not part of the collaboration: $researcherId")

        //TODO muito legal que você esteja pegando a ideia de funções, mas acho que para esse caso não fica mais
        // claro se fizer diretamente?

        //requireThatExists(containsCollaborator(researcherId)) {
        //    "Cannot remove member that is not part of the collaboration"
        //}
        _collaborators.remove(researcherId)
    }

    override fun toString() = "SystematicStudy(reviewId=$reviewId, title='$title', description='$description', " +
            "owner=$owner, _collaborators=$_collaborators)"
}