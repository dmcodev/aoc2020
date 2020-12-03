package day01

import util.loadInputLongs

private fun <T> Collection<T>.tuples(size: Int): Sequence<List<T>> {
    fun expand(
        previousElementIndex: Int,
        values: List<T>
    ): Sequence<List<T>> =
        if (values.size == size)
            sequenceOf(values)
        else
            asSequence()
                .drop(previousElementIndex + 1)
                .flatMapIndexed { index, value -> expand(index + previousElementIndex + 1, values + value) }
    return expand(-1, emptyList())
}

private const val magicValue = 2020L

fun main() {
    println(
        loadInputLongs(1)
            .tuples(2)
            .first { it[0] + it[1] == magicValue }
            .let { it[0] * it[1] }
    )
    println(
        loadInputLongs(1)
            .tuples(3)
            .first { it[0] + it[1] + it[2] == magicValue }
            .let { it[0] * it[1] * it[2] }
    )
}