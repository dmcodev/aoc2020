package day11

import util.loadInput

private typealias Vector = Pair<Int, Int>

class Fields(input: List<String>) {

    private val map: MutableMap<Vector, Char> = input.asSequence()
        .flatMapIndexed { rowIndex, row ->
            row.asSequence()
                .mapIndexed { columnIndex, char -> Triple(columnIndex, rowIndex, char) }
        }
        .associateByTo(mutableMapOf(), { it.first to it.second }) { it.third }

    private val visibilityMap = mutableMapOf<Pair<Vector, Vector>, Vector?>()

    private val width = map.keys.map { it.first }.maxOrNull()!! + 1
    private val height = map.keys.map { it.second }.maxOrNull()!! + 1

    private val neighbourVectors = listOf(-1 to -1, 0 to -1, 1 to -1, 1 to 0, 1 to 1, 0 to 1, -1 to 1, -1 to 0)

    fun adjacentRound(): Int =
        round(::adjacentNeighbours, 4)

    fun visibilityRound(): Int =
        round(::visibleNeighbours, 5)

    fun countOccupied(): Int =
        map.values.count { it.isOccupied() }

    private fun round(neighbours: (Vector) -> Sequence<Vector>, occupiedPlacesLimit: Int): Int {
        val placesToOccupy = map.keys.asSequence()
            .filter { map.getValue(it).isEmpty() }
            .filter { baseVector -> neighbours(baseVector).none { it.value().isOccupied() } }
            .toSet()
        val placesToVacate = map.keys.asSequence()
            .filter { !placesToOccupy.contains(it) }
            .filter { map.getValue(it).isOccupied() }
            .filter { baseVector -> neighbours(baseVector).count { it.value().isOccupied() } >= occupiedPlacesLimit }
            .toSet()
        placesToOccupy.forEach { map[it] = '#' }
        placesToVacate.forEach { map[it] = 'L' }
        return placesToOccupy.size + placesToVacate.size
    }

    private fun adjacentNeighbours(vector: Pair<Int, Int>): Sequence<Vector> =
        neighbourVectors.asSequence()
            .map { vector + it }
            .filter { it.valid() }

    private fun visibleNeighbours(vector: Pair<Int, Int>): Sequence<Vector> =
        neighbourVectors.asSequence()
            .map { visibleFrom(vector, it) }
            .filterNotNull()

    private fun visibleFrom(vector: Vector, delta: Vector): Vector? {
        return visibilityMap.computeIfAbsent(vector to delta) {
            val next = vector + delta
            if (next.valid())
                if (next.value().isNotFloor()) next else visibleFrom(next, delta)
            else null
        }
    }

    private fun Char.isEmpty() = this == 'L'
    private fun Char.isOccupied() = this == '#'
    private fun Char.isNotFloor() = this != '.'

    private fun Vector.x() = first
    private fun Vector.y() = second
    private operator fun Vector.plus(other: Pair<Int, Int>) = (x() + other.x()) to (y() + other.y())
    private fun Vector.valid() = (first in 0 until width) && (second in 0 until height)
    private fun Vector.value() = map.getValue(this)
}


fun main() {
    partOne()
    partTwo()
}

fun partOne() {
    val fields = Fields(loadInput(11))
    while (fields.adjacentRound() > 0) {}
    println(fields.countOccupied())
}

fun partTwo() {
    val fields = Fields(loadInput(11))
    while (fields.visibilityRound() > 0) {}
    println(fields.countOccupied())
}