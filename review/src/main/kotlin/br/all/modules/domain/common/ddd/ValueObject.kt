package br.all.modules.domain.common.ddd

abstract class ValueObject {
    protected abstract fun validate() : Notification
}
