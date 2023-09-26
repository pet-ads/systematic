package br.all.domain.shared.ddd

import br.all.domain.shared.utils.Language
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals

class LanguageTest {

    @Test
    fun `valid Language - English`() {
        val language = Language(Language.LangType.ENGLISH)
        assertEquals(Language.LangType.ENGLISH, language.langType)
    }

    @Test
    fun `valid Language - Portuguese`() {
        val language = Language(Language.LangType.PORTUGUESE)
        assertEquals(Language.LangType.PORTUGUESE, language.langType)
    }

    @Test
    fun `should be equals`() {
        val language1 = Language(Language.LangType.PORTUGUESE)
        val language2 = Language(Language.LangType.PORTUGUESE)
        assertEquals(language1, language2)
    }

    @Test
    fun `should not be equals`() {
        val language1 = Language(Language.LangType.PORTUGUESE)
        val language2 = Language(Language.LangType.ENGLISH)
        assertNotEquals(language1, language2)
    }


    @Test
    fun `validate Language - Invalid Language Type`() {
        assertFailsWith<IllegalArgumentException> {
            Language(Language.LangType.valueOf("INVALID_TYPE"))
        }
    }
}