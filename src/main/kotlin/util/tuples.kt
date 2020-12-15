package util

fun <T> Collection<T>.tuples(size: Int): Sequence<List<T>> {
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