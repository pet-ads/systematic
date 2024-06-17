package br.all.domain.shared.ddd

class Notification {

    private val errors: MutableList<Error> = mutableListOf()
    fun addError(message: String, cause: Exception? = null) = errors.add(Error(message, cause))
    fun hasNoErrors() = errors.isEmpty()
    fun message() = errors.joinToString(" | ") { it.message }
    data class Error(val message: String, val cause: Exception?)
}



