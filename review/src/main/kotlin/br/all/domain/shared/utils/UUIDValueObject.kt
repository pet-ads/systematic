package br.all.domain.shared.utils

import br.all.domain.shared.ddd.Notification
import br.all.domain.shared.ddd.ValueObject
import java.util.*

//TODO: what is this class for? UUID is a value to be passed to value objects, not a value object to be implemented itself.
abstract class UUIDValueObject(open val value: UUID) : ValueObject() {

    init {
        val notification = validate()
        require(notification.hasNoErrors()) {notification.message()}
    }

    //TODO use the Kotlin way
    override fun validate() = Notification()
    //TODO not the Java way

    //override fun validate(): Notification { return Notification() }
}