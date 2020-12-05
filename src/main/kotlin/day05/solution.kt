package day05

import util.loadInput
import kotlin.math.max

fun IntRange.middle(): Int =
    (last - first) / 2

fun IntRange.downHalf(): IntRange =
    first .. (first + middle())

fun IntRange.upHalf(): IntRange =
    (first + middle() + 1) .. last

fun main() {
    val input = loadInput(5)
    val rowRange = 0 .. 127
    val columnRange = 0 .. 7
    var maxId = 0
    val seatMap = mutableMapOf<Int, MutableSet<Int>>()
    for (line in input) {
        val row = line.substring(0, 7)
            .fold(rowRange) { it, char -> if (char == 'F') it.downHalf() else it.upHalf() }
            .first
        val column = line.substring(7)
            .fold(columnRange) { it, char -> if (char == 'L') it.downHalf() else it.upHalf() }
            .first
        seatMap.computeIfAbsent(row) { mutableSetOf() }.add(column)
        maxId = max(maxId, row * 8 + column)
    }
    val myRow = seatMap.asSequence().first { it.value.size == 7 }
    val myColumn = columnRange.first { !myRow.value.contains(it) }
    val myId = myRow.key * 8 + myColumn
    println(maxId)
    println(myId)
}