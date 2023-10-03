package br.all.application.shared.utils

fun requireThatExists(doesItExist: Boolean, message: () -> String = { "There is no such element!" }) {
    if (!doesItExist) throw NoSuchElementException(message())
}

fun <T> requireThatExists(value: () -> T?, message: () -> String = { "There is not such element" }) : T =
    value() ?: throw NoSuchElementException(message())
