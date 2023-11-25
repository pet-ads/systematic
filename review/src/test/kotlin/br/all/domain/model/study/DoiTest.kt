package br.all.domain.model.study

import org.junit.jupiter.api.Assertions.*
import kotlin.test.Test

class DoiTest {

    @Test
    fun `Should throw if starts without prefix`() {
        assertThrows(IllegalArgumentException::class.java) { Doi("1080/01621459") }
    }

    @Test
    fun `Should accept valid doi`() {
        assertDoesNotThrow { Doi("https://doi.org/10.1109/5.771073") }
    }
}