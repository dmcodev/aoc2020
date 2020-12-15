package day15

import util.loadInput

fun main() {
    val input = loadInput(15)[0].split(",").map { it.toLong() }
    partOne(input)
    partTwo(input)
}

private fun partOne(input: List<Long>) = solve(input, 2020)
private fun partTwo(input: List<Long>) = solve(input, 30000000)

private class NumberHistory {

    private val history = IntArray(2) { 0 }

    fun addTurn(turn: Int) {
        history[0] = history[1]
        history[1] = turn
    }

    fun spokenBefore() = history[0] != 0

    fun turnsApart() = (history[1] - history[0]).toLong()
}

private class History(input: List<Long>) {

    private val history = mutableMapOf<Long, NumberHistory>()

    init {
        input.forEachIndexed { turn, number -> numberHistory(number).addTurn(turn + 1) }
    }

    fun numberSpokenBefore(number: Long) = numberHistory(number).spokenBefore()

    fun numberTurnsApart(number: Long) = numberHistory(number).turnsApart()

    fun addNumberTurn(number: Long, turn: Int) = numberHistory(number).addTurn(turn)

    private fun numberHistory(number: Long) = history.computeIfAbsent(number) { NumberHistory() }
}

private fun solve(input: List<Long>, stopAfterTurn: Int) {
    val history = History(input)
    var turn = input.size
    var last = input.last()
    while (++turn <= stopAfterTurn) {
        last = if (history.numberSpokenBefore(last)) history.numberTurnsApart(last) else 0L
        history.addNumberTurn(last, turn)
    }
    println(last)
}