package br.all.domain.shared.utils

inline fun requireThatExists(doesItExist: Boolean, message: () -> String = { "There is no such element!" }) {
    if (!doesItExist) throw NoSuchElementException(message())
}

inline fun <T> requireThatExists(value: T?, message: () -> String = { "There is not such element" }) = value ?:
    throw NoSuchElementException(message())
