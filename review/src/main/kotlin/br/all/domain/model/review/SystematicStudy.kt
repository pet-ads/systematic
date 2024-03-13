package br.all.domain.model.review

import br.all.domain.model.researcher.ResearcherId
import br.all.domain.shared.ddd.Entity
import br.all.domain.shared.ddd.Notification
import br.all.domain.shared.utils.exists
import java.util.*

class SystematicStudy(
    id: SystematicStudyId,
    title: String,
    description: String,
    owner: ResearcherId,
    collaborators: Set<ResearcherId> = emptySet(),
) : Entity<UUID>(id) {

    private val _collaborators = collaborators.toMutableSet()
    val collaborators get() = _collaborators.toSet()
    var owner = owner
        private set
    var title: String = title
        set(value) {
            require(value.isNotBlank()) { "Systematic study title must not be blank." }
            field = value
        }
    var description = description
        set(value) {
            require(value.isNotBlank()) { "Systematic study description must not be blank." }
            field = value
        }

    init {
        val notification = validate()
        require(notification.hasNoErrors()) { notification.message() }
        _collaborators.add(owner)
    }

    private fun validate() = Notification().also {
        if (title.isBlank())
            it.addError("Systematic Study title must not be blank!")
        if (description.isBlank())
            it.addError("Systematic Study description must not be blank!")
    }

    fun addCollaborator(researcherId: ResearcherId) = _collaborators.add(researcherId)

    fun changeOwner(researcherId: ResearcherId) {
        _collaborators.add(researcherId)
        owner = researcherId
    }

    fun removeCollaborator(researcherId: ResearcherId) {
        check(researcherId != owner) { "Cannot remove the Systematic Study owner: $owner" }
        exists(researcherId in _collaborators) {
            "Cannot remove member that is not part of the collaboration: $researcherId"
        }
        _collaborators.remove(researcherId)
    }

    override fun toString() = "SystematicStudy(reviewId=$id, title='$title', description='$description', owner=$owner," +
            " researchers=$_collaborators)"

    companion object
}
