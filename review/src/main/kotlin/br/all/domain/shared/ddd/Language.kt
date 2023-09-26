package br.all.domain.shared.ddd

data class Language(val langType: LangType) : ValueObject() {

    init {
        val notification = validate()

        require(notification.hasNoErrors()) {notification.message()}
    }
    enum class LangType {
        ENGLISH,
        SPANISH,
        FRENCH,
        GERMAN,
        ITALIAN,
        PORTUGUESE
    }

    override fun validate(): Notification { return Notification() }

}
