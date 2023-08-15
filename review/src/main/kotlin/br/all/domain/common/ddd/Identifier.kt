package br.all.domain.common.ddd


interface Identifier {
    fun validate() : Notification
}