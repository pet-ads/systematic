package br.all.domain.shared.utils

import br.all.domain.shared.ddd.Notification
import br.all.domain.shared.ddd.ValueObject

// TODO I would represent the entire VO using only the Enum. See a Java example:
// https://github.com/lucas-ifsp/CTruco/blob/master/domain/src/main/java/com/bueno/domain/entities/deck/Rank.java
data class Language(val langType: LangType) : ValueObject() {

    init {
        val notification = validate()
        require(notification.hasNoErrors()) {notification.message()}
    }

    //TODO it will be a real online system (I hope so). I expect the same amount of possible languages as any web site
    // would support. Go hard or do home! =D
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
