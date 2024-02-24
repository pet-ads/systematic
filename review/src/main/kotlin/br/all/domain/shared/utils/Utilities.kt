package br.all.domain.shared.utils

inline fun exists(exist: Boolean, message: () -> String = { "There is no such element!" }) {
    if (!exist) throw NoSuchElementException(message())
}
