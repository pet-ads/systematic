package br.all.domain.shared.ddd

import br.all.domain.shared.valueobject.Text
import org.junit.jupiter.api.*
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

@Tag("UnitTest")
class TextTest {

    @Test
    fun `valid TEXT should not throw an exception`() {
        val textValue = "Mussum Ipsum, cacilds vidis litro abertis.  Nulla id gravida magna, ut semper sapien. Todo mundo vê os porris que eu tomo, mas ninguém vê os tombis que eu levo! Leite de capivaris, leite de mula manquis sem cabeça. Admodum accumsan disputationi eu sit. Vide electram sadipscing et per. Si num tem leite então bota uma pinga aí cumpadi! A ordem dos tratores não altera o pão duris. Negão é teu passadis, eu sou faxa pretis. Nullam volutpat risus nec leo commodo, ut interdum diam laoreet. Sed non consequat odio."
        val text = Text(textValue)
    }

    @Test
    fun `equal TEXTs should be equal`() {
        val textValue1 = "Mussum Ipsum, cacilds vidis litro abertis.  Delegadis gente finis, bibendum egestas augue arcu ut est. Negão é teu passadis, eu sou faxa pretis. Manduma pindureta quium dia nois paga. Interessantiss quisso pudia ce receita de bolis, mais bolis eu num gostis."
        val textValue2 = "Mussum Ipsum, cacilds vidis litro abertis.  Delegadis gente finis, bibendum egestas augue arcu ut est. Negão é teu passadis, eu sou faxa pretis. Manduma pindureta quium dia nois paga. Interessantiss quisso pudia ce receita de bolis, mais bolis eu num gostis."

        val text1 = Text(textValue1)
        val text2 = Text(textValue2)

        assertEquals(text1, text2)
    }

    @Test
    fun `diferent TEXTs should not be equal`() {
        val textValue1 = "Mussum Ipsum, cacilds vidis litro abertis.  Delegadis gente finis, bibendum egestas augue arcu ut est. Negão é teu passadis, eu sou faxa pretis. Manduma pindureta quium dia nois paga. Interessantiss quisso pudia ce receita de bolis, mais bolis eu num gostis."
        val textValue2 = "Mussum Ipsum, cacilds vidis litro abertis.  Per aumento de cachacis, eu reclamis. Tá deprimidis, eu conheço uma cachacis que pode alegrar sua vidis. Nulla id gravida magna, ut semper sapien. Aenean aliquam molestie leo, vitae iaculis nisl."

        val text1 = Text(textValue1)
        val text2 = Text(textValue2)

        assertNotEquals(text1, text2)
    }

    @Test
    fun `valid text with special characters should stil valid`() {
        val textValue = "Mussum Ipsum, cacilds vidis litro abertis.  Nulla id gravida magna, ut semper sapien. Todo mundo vê os porris que eu tomo, mas ninguém vê os tombis que eu levo! Leite de capivaris, leite de mula manquis sem cabeça. Admodum accumsan disputationi eu sit. Vide electram sadipscing et per. Si num tem leite então bota uma pinga aí cumpadi! A ordem dos tratores não altera o pão duris. Negão é teu passadis, eu sou faxa pretis. Nullam volutpat risus nec leo commodo, ut interdum diam laoreet. Sed non consequat odio."
        val text = Text(textValue)
        val newTextValue = textValue + "(test!@#$%)"
        assertDoesNotThrow {
            Text(textValue)
            Text(newTextValue)
        }

    }

    @Test
    fun `empty TEXT should throw an exception`() {
        val textValue = ""
        assertThrows<IllegalArgumentException> {
            Text(textValue)
        }
    }

    @Test
    fun `blank TEXT should throw an exception`() {
        val textValue = "      "
        assertThrows<IllegalArgumentException> {
            Text(textValue)
        }
    }

    @Nested
    @DisplayName("should NOT be able to accept as TEXT")
    inner class InvalidTextTest {


        @Test
        fun `@@@`() {
            val textValue = "@@@"
            assertThrows<IllegalArgumentException> {
                Text(textValue)
            }
        }
        @Test
        @DisplayName(".")
        fun ShouldNotAcceptDotsAsText() {
            val textValue = "."
            assertThrows<IllegalArgumentException> {
                Text(textValue)
            }
        }
        @Test
        @DisplayName(",")
        fun ShouldNotAcceptCommasAsText() {
            val textValue = "."
            assertThrows<IllegalArgumentException> {
                Text(textValue)
            }
        }
        @Test
        fun `!!!`() {
            val textValue = "!!!"
            assertThrows<IllegalArgumentException> {
                Text(textValue)
            }
        }
        @Test
        fun `###`() {
            val textValue = "###"
            assertThrows<IllegalArgumentException> {
                Text(textValue)
            }
        }
        @Test
        fun `$$$`() {
            val textValue = "$$$"
            assertThrows<IllegalArgumentException> {
                Text(textValue)
            }
        }
        @Test
        fun `%%%`() {
            val textValue = "%%%"
            assertThrows<IllegalArgumentException> {
                Text(textValue)
            }
        }
        @Test
        @DisplayName(":")
        fun ShouldNotAcceptColonsAsText() {
            val textValue = ":"
            assertThrows<IllegalArgumentException> {
                Text(textValue)
            }
        }
        @Test
        @DisplayName(";")
        fun `ShouldNotAcceptSemicolonsAsText`() {
            val textValue = ";"
            assertThrows<IllegalArgumentException> {
                Text(textValue)
            }
        }
        @Test
        @DisplayName("[")
        fun `ShouldNotAcceptBracesText`() {
            val textValue = "[["
            assertThrows<IllegalArgumentException> {
                Text(textValue)
            }
        }
        @Test
        @DisplayName("]")
        fun `ShouldNotAcceptCloseBracesText`() {
            val textValue = "]]"
            assertThrows<IllegalArgumentException> {
                Text(textValue)
            }
        }

        @Test
        @DisplayName("#\$%!;")
        fun `ShouldNotAcceptJustSymbolsText`() {
            val textValue = "#$%!;"
            assertThrows<IllegalArgumentException> {
                Text(textValue)
            }
        }

        @Test
        @DisplayName("[]")
        fun ShouldNotAcceptBraceText() {
            val textValue = "[[[]]]"
            assertThrows<IllegalArgumentException> {
                Text(textValue)
            }
        }

    }
}
