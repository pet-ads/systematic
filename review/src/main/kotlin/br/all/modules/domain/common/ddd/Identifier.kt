package br.all.modules.domain.common.ddd


interface Identifier {
    fun validate() : Notification
}