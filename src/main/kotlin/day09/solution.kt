package day09

import util.loadInputLongs
import util.tuples
import java.util.*

private const val PREAMBLE_LENGTH = 25

fun main() {
    val input = loadInputLongs(9)
    val queue = LinkedList<Long>()
    queue.addAll(input.subList(0, PREAMBLE_LENGTH))
    val invalidNumber = input.asSequence()
        .drop(PREAMBLE_LENGTH)
        .map { number ->
            if (queue.tuples(2).filter { it[0] != it[1] }.map { it[0] + it[1] }.none { it == number }) {
                number
            } else {
                queue.removeFirst()
                queue.addLast(number)
                null
            }
        }
        .filterNotNull()
        .first()
    val weakness = input.indices.asSequence()
        .map { start ->
            var sum = 0L
            val end = input.asSequence()
                .drop(start)
                .takeWhile { sum + it < invalidNumber }
                .onEach { sum += it }
                .count()
                .plus(start)
            if (end - start >= 2 && sum + input[end] == invalidNumber) {
                val range = input.subList(start, end + 1)
                range.minOrNull()!! + range.maxOrNull()!!
            } else null
        }
        .filterNotNull()
        .first()
    println(invalidNumber)
    println(weakness)
}