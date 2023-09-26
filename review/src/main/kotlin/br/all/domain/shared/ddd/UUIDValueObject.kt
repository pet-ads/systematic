package br.all.domain.shared.ddd

import java.util.*

abstract class UUIDValueObject(open val value: UUID) : ValueObject() {
    init {
        val notification = validate()

        require(notification.hasNoErrors()) {notification.message()}
    }
    override fun validate(): Notification { return Notification() }
}