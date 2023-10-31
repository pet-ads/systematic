package br.all.domain.shared.utils

import java.util.function.Predicate

class NeverEmptyMutableSet<T>(
    vararg source: T
): MutableSet<T> by mutableSetOf() {
    init {
        require(source.isNotEmpty()) { "Unable to create a NeverEmptyMutableSet from an empty source of elements!" }
        addAll(source)
    }

    override fun isEmpty() = false

    override fun clear() = clear(elementAt(0))

    fun clear(keepingElement: T) {
        removeIf{ it != keepingElement }
    }

    override fun remove(element: T): Boolean {
        check(size > 1) { "Unable to remove elements so far because it would cause this set to be empty!" }
        return removeIf { it == element }
    }

    override fun removeAll(elements: Collection<T>): Boolean {
        require(elements.size < size || elements.size == size && !containsAll(elements))
            { "Unable to remove elements: $elements; because it would cause this set to be empty!" }
        return removeIf{ it in elements }
    }

    override fun retainAll(elements: Collection<T>): Boolean {
        require(elements.isEmpty() || elements.none{ it in this }) {
            "At least one element should be kept in this set! The provided collection: $elements; would cause " +
                    "in all elements being removed!"
        }
        return removeIf{ it in elements }
    }

    override fun removeIf(filter: Predicate<in T>): Boolean {
        var somethingWasRemoved = false
        val iterator = iterator()

        while (iterator.hasNext() && size > 1) {
            val element = iterator.next()

            if (filter.test(element)) {
                somethingWasRemoved = true
                remove(element)
            }
        }

        return somethingWasRemoved
    }

    operator fun get(index: Int) = elementAt(index)

    fun toMutableSet() = mutableSetOf(this)
}

fun <T> Set<T>.toNeverEmptyMutableSet() = NeverEmptyMutableSet(elementAt(0)).also { it.addAll(this) }

fun <T> neverEmptyMutableSetOf(element: T, vararg elements: T) = NeverEmptyMutableSet(element, elements)
