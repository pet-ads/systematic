package br.all.domain.shared.ddd

abstract class ValueObject {
    protected abstract fun validate() : Notification
}
