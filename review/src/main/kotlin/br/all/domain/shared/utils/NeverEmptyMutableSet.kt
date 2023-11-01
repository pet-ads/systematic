package br.all.domain.shared.utils

import java.util.function.Predicate

class NeverEmptyMutableSet<T> private constructor(
    private val innerSet: MutableSet<T>,
    source: Collection<T>,
): MutableSet<T> by innerSet {
    constructor(vararg source: T) : this(source.toSet())
    constructor(source: Collection<T>) : this(mutableSetOf(), source = source)

    init {
        require(source.isNotEmpty()) { "Unable to create a NeverEmptyMutableSet from an empty source of elements!" }
        addAll(source)
    }

    override fun isEmpty() = false

    override fun clear() = clear(elementAt(0))

    fun clear(keepingElement: T) {
        require(keepingElement in innerSet) { "Unable to clear the set because the given element to keep " +
                "(keepingElement=$keepingElement) are not in this set!" }
        innerSet.retainAll { it == keepingElement }
    }

    override fun remove(element: T): Boolean {
        if (element !in innerSet) return false
        check(size > 1) { "Unable to remove elements so far because it would cause this set to be empty!" }
        return innerSet.remove(element)
    }

    override fun removeAll(elements: Collection<T>): Boolean {
        val elementsAsSet = elements.toSet()
        require(elementsAsSet.size < size || innerSet.intersect(elementsAsSet).size < size)
            { "Unable to remove elements: $elements; because it would cause this set to be empty!" }
        return innerSet.removeAll(elementsAsSet)
    }

    override fun retainAll(elements: Collection<T>): Boolean {
        require(elements.isEmpty() || elements.none{ it in innerSet }) {
            "At least one element should be kept in this set! The provided collection: $elements; would cause " +
                    "in all elements being removed!"
        }
        return innerSet.retainAll(elements.toSet())
    }

    override fun removeIf(filter: Predicate<in T>): Boolean {
        return innerSet.removeIf(filter.and { size > 1 })
    }

    operator fun get(index: Int) = elementAt(index)

    fun toMutableSet() = innerSet.toMutableSet()

    override fun toString() = innerSet.joinToString(", ", "[", "]")
}

fun <T> Set<T>.toNeverEmptyMutableSet() = NeverEmptyMutableSet(elementAt(0)).also { it.addAll(this) }

fun <T> neverEmptyMutableSetOf(element: T, vararg elements: T) = NeverEmptyMutableSet(
    elements.toMutableSet().also {it.add(element) }
)
