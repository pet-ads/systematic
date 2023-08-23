package br.all.domain.shared.ddd


interface Identifier {
    fun validate() : Notification
}