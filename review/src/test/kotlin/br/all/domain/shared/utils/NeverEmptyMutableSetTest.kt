package br.all.domain.shared.utils

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class NeverEmptyMutableSetTest {
    @Test
    fun `Should throw if attempting to create an NeverEmptyMutableSet from an empty source`() {
        assertThrows<IllegalArgumentException> { NeverEmptyMutableSet<Int>() }
    }
    
    @Test
    fun `Should keep at least one elements when clearing an NeverEmptyMutableSet`() {
        val sut = neverEmptyMutableSetOf(10, 25, 30, 88)
        sut.clear()
        assertEquals(1, sut.size)
    }
    
    @Test
    fun `Should keep the correct element when clearing an NeverEmptyMutableSet`() {
        val sut = neverEmptyMutableSetOf(10, 25, 30, 88)
        sut.clear(30)
        assertAll(
            { assertEquals(1, sut.size) },
            { assertEquals(30, sut.first()) }
        )
    }
    
    @Test
    fun `Should successfully create an NeverEmptyMutableSet of Int`() {
        val sut = neverEmptyMutableSetOf(10, 20, 30, 40, 50)
        assertAll(
            { assertContains(sut, 10) },
            { assertContains(sut, 20) },
            { assertContains(sut, 30) },
            { assertContains(sut, 40) },
            { assertContains(sut, 50) },
        )
    }
    
    @Test
    fun `Should throw IllegalStateException when trying to remove an element when there is only one remaining`() {
        val sut = neverEmptyMutableSetOf(10)
        assertThrows<IllegalStateException> { sut.remove(10) }
    }

    @Test
    fun `Should successfully remove an element if it exists and it will not cause in empty set`() {
        val sut = neverEmptyMutableSetOf(10, 20)
        assertTrue { sut.remove(20) }
    }

    @Test
    fun `Should do nothing when trying to remove an nonexistent element`() {
        val sut = neverEmptyMutableSetOf(10)
        assertFalse { sut.remove(20) }
    }

    @Test
    fun `Should remove all elements listed in a collection if its size is lower than the set`() {
        val sut = neverEmptyMutableSetOf(10, 20, 30, 40, 50)

        sut.removeAll(setOf(30, 40, 50))

        assertEquals(2, sut.size)
    }

    @Test
    fun `Should remove all elements if number of elements that are in the set are lower than the set size`() {
        val sut = neverEmptyMutableSetOf(10, 20, 30, 40, 50)

        assertAll(
            { assertDoesNotThrow { sut.removeAll(setOf(10, 20, 30, 60, 70, 80)) } },
            { assertEquals(2, sut.size) }
        )
    }
}
