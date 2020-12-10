package day10

import util.loadInputLongs

private val DIFF_RANGE = 1 .. 3

fun main() {
    val input = listOf(0L).plus(loadInputLongs(10)).sorted()
    val diffs = input.indices.asSequence()
        .drop(1)
        .map { input[it] - input[it - 1] }
        .plus(3)
        .groupingBy { it }
        .eachCount()
    println(diffs.getValue(1) * diffs.getValue(3))
    val count = mutableMapOf<Int, Long>()
    fun connects(index: Int): Long =
        count.computeIfAbsent(index) {
            if (index == input.size - 1)
                1L
            else
                (index + 1 until input.size)
                    .takeWhile { (input[it] - input[index]) in DIFF_RANGE }
                    .map { connects(it) }
                    .sum()
        }
    println(connects(0))
}