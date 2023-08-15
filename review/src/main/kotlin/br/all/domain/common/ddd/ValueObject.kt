package br.all.domain.common.ddd

abstract class ValueObject {
    protected abstract fun validate() : Notification
}
