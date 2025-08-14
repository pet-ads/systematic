package br.all.domain.shared.ddd

interface Identifier <T>{
    fun validate() : Notification
    fun value() : T
}