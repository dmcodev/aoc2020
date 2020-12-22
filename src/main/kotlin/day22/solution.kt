package day22

import util.loadInput

private fun playSimple(cardsOne: ArrayDeque<Int>, cardsTwo: ArrayDeque<Int>): Boolean {
    while (cardsOne.isNotEmpty() && cardsTwo.isNotEmpty()) {
        val cardOne = cardsOne.removeFirst()
        val cardTwo = cardsTwo.removeFirst()
        if (cardOne > cardTwo) {
            cardsOne.addAll(listOf(cardOne, cardTwo))
        } else {
            cardsTwo.addAll(listOf(cardTwo, cardOne))
        }
    }
    return cardsTwo.isEmpty()
}

private fun play(cardsOne: ArrayDeque<Int>, cardsTwo: ArrayDeque<Int>): Boolean {
    val history = mutableSetOf<Collection<Collection<Int>>>()
    while (cardsOne.isNotEmpty() && cardsTwo.isNotEmpty()) {
        val setup = listOf(cardsOne.toList(), cardsTwo.toList())
        if (history.contains(setup)) return true
        history.add(setup)
        val cardOne = cardsOne.removeFirst()
        val cardTwo = cardsTwo.removeFirst()
        val firstPlayerWon = if (cardsOne.size >= cardOne && cardsTwo.size >= cardTwo) {
            play(ArrayDeque(cardsOne.take(cardOne)), ArrayDeque(cardsTwo.take(cardTwo)))
        } else cardOne > cardTwo
        if (firstPlayerWon) {
            cardsOne.addAll(listOf(cardOne, cardTwo))
        } else {
            cardsTwo.addAll(listOf(cardTwo, cardOne))
        }
    }
    return cardsTwo.isEmpty()
}

fun main() {
    val input = loadInput(22)
    val cardsOne = input.subList(1, 26).map { it.toInt() }
    val cardsTwo = input.subList(28, 53).map { it.toInt() }
    partOne(ArrayDeque(cardsOne), ArrayDeque(cardsTwo))
    partTwo(ArrayDeque(cardsOne), ArrayDeque(cardsTwo))
}

private fun partOne(cardsOne: ArrayDeque<Int>, cardsTwo: ArrayDeque<Int>) {
    val winnerCards = if (playSimple(cardsOne, cardsTwo)) cardsOne else cardsTwo
    println(winnerCards.reversed().mapIndexed { index, value -> (index + 1) * value }.sum())
}

private fun partTwo(cardsOne: ArrayDeque<Int>, cardsTwo: ArrayDeque<Int>) {
    val winnerCards = if (play(cardsOne, cardsTwo)) cardsOne else cardsTwo
    println(winnerCards.reversed().mapIndexed { index, value -> (index + 1) * value }.sum())
}

