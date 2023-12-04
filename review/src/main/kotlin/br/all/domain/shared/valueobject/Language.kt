package br.all.domain.shared.valueobject

import br.all.domain.shared.ddd.Notification
import br.all.domain.shared.ddd.ValueObject

// TODO I would represent the entire VO using only the Enum. See a Java example:
// https://github.com/lucas-ifsp/CTruco/blob/master/domain/src/main/java/com/bueno/domain/entities/deck/Rank.java
data class Language(val langType: LangType) : ValueObject() {

    init {
        val notification = validate()
        require(notification.hasNoErrors()) {notification.message()}
    }

    enum class LangType {
        AFRIKAANS,
        AMHARIC,
        ARABIC,
        AZERBAIJANI,
        BENGALI,
        BULGARIAN,
        BURMESE,
        CZECH,
        DANISH,
        ENGLISH,
        ESTONIAN,
        FAROENSE,
        FARSI,
        FILIPINO,
        FINNISH,
        FRENCH,
        FULA,
        GEORGIAN,
        GERMAN,
        GREEK,
        GREENLANDIC,
        HAUSA,
        HEBREW,
        HINDI,
        HUNGARIAN,
        ICELANDIC,
        IGBO,
        INDONESIAN,
        ITALIAN,
        JAPANESE,
        JAVANESE,
        KAZAKH,
        KHMER,
        KOREAN,
        KYRGYZ,
        LAO,
        LATVIAN,
        LITHUANIAN,
        MALAY,
        MALTESE,
        MANDARIN_CHINESE,
        MARATHI,
        MONGOLIAN,
        NEPALI,
        NORWEGIAN,
        PASHTO,
        PERSIAN,
        POLISH,
        PORTUGUESE,
        PUNJABI,
        ROMANIAN,
        RUSSIAN,
        SERBO_CROATIAN,
        SINHALA,
        SLOVAK,
        SLOVENE,
        SPANISH,
        SWAHILI,
        SWEDISH,
        TAMIL,
        TATAR,
        TELUGU,
        THAI,
        TIBETAN,
        TURKISH,
        TURKMEN,
        UKRAINIAN,
        URDU,
        UZBEK,
        VIETNAMESE,
        WOLLOF,
        YIDDISH,
        YORUBA,
        ZULU
    }

    override fun validate(): Notification { return Notification() }

}
