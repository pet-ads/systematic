package br.all.domain.shared.utils

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class NeverEmptyMutableSetTest {
    @Test
    fun `Should throw if attempting to create an NeverEmptyMutableSet from an empty source`() {
        assertThrows<IllegalArgumentException> { NeverEmptyMutableSet<Int>() }
    }
}
