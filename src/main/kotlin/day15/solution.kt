package day15

import util.loadInput

fun main() {
    partOne()
    partTwo()
}

private fun partOne() = solve(2020)
private fun partTwo() = solve(30000000)

private fun solve(stopAfterTurn: Int) {
    val input = loadInput(15)[0].split(",").map { it.toLong() }
    val history = mutableMapOf<Long, MutableList<Int>>()
    fun historyFor(number: Long) = history.computeIfAbsent(number) { mutableListOf() }
    input.forEachIndexed { turn, number -> historyFor(number).add(turn + 1) }
    var turn = input.size + 1
    var last = input.last()
    while (turn <= stopAfterTurn) {
        val next = if (history[last]?.size ?: 0 < 2) {
            0L
        } else {
            historyFor(last).let { it[it.size - 1].toLong() - it[it.size - 2] }
        }
        historyFor(next).add(turn)
        last = next
        turn++
    }
    println(last)
}