package br.all.domain.common.ddd

abstract class Entity (private val id : Identifier) {

    final override fun equals(other: Any?) = when{
        this === other ->  true
        other !is Entity -> false
        else -> id ==  other.id
    }

    final override fun hashCode() = id.hashCode()
}