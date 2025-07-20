package br.all.domain.shared.utils

import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.assertAll
import kotlin.test.Test
import kotlin.test.assertEquals

@Tag("UnitTest")
class NormalizeTextTest {

    @Test
    fun `should remove special characters and accents`() {
        assertAll(
            { assertEquals("cafe creme", normalizeText("café crème")) },
            { assertEquals("uber strae", normalizeText("über straße")) },
            { assertEquals("senor pinata", normalizeText("señor piñata")) }
        )
    }

    @Test
    fun `should remove multiple spaces and punctuations`() {
        assertAll(
            { assertEquals("hello world how are you", normalizeText("Hello,    World!!!   How   are   you?")) },
            { assertEquals("firstsecond third fourth", normalizeText("First.Second  Third   Fourth")) }
        )
    }

    @Test
    fun `should remove symbols and mixed cases`() {
        assertAll(
            { assertEquals("myemailcom", normalizeText("My@Email.com")) },
            { assertEquals("username123", normalizeText("USER_NAME-123")) }
        )
    }

    @Test
    fun `should remove non-unicode characters`() {
        assertAll(
            { assertEquals("i love prgramming", normalizeText("♥ I löve prøgramming! ♥")) },
            { assertEquals("resume cv", normalizeText("résumé (CV)")) }
        )
    }

    @Test
    fun `testing multiple possibilities`() {
        assertAll(
            { assertEquals("start end", normalizeText("    Start   &   End    ")) },
            { assertEquals("kr cjava 2023", normalizeText("K&R C++/Java [2023]")) },
            { assertEquals("joao sao paulo", normalizeText("João São Paulo")) },
            { assertEquals("francois munich", normalizeText("François Münich")) }
        )
    }
}