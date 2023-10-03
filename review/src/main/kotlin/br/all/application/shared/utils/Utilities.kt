package br.all.application.shared.utils

fun requireThatExists(doesItExist: Boolean, message: () -> String = { "There is no such element!" }) {
    if (!doesItExist) throw NoSuchElementException(message())
}
