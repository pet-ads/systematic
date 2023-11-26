package br.all.infrastructure.shared

import java.util.*

fun <T> Optional<T>.toNullable(): T? = orElse(null)
