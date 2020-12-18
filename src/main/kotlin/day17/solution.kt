package day17

import util.loadInput

private typealias Vector = List<Int>

private data class Point(val vector: Vector, var active: Boolean)

private class Space(private val dimensions: Int) {

    private val zeroVector = generateSequence { 0 }.take(dimensions).toList()
    private val points = mutableMapOf<Vector, Point>()
    private val neighbours = mutableMapOf<Vector, List<Point>>()

    fun neighbours(point: Point) =
        neighbours.computeIfAbsent(point.vector) {
            neighbourhood(0, point.vector, point.vector)
                .filter { it != point.vector }
                .map { vector -> points.computeIfAbsent(vector) { Point(it, false) } }
                .toList()
        }

    fun add(vector: List<Int>, active: Boolean) {
        val point = Point(vector.plus(zeroVector).take(dimensions), active)
        points[point.vector] = point
    }

    private fun neighbourhood(index: Int, center: Vector, current: Vector): Sequence<Vector> {
        if (index == dimensions) return sequenceOf(current)
        val centerCoordinate = center[index]
        val movedCoordinates = { alteration: Int -> current.toMutableList().also { it[index] = centerCoordinate + alteration } }
        return sequenceOf(-1, 0, 1).flatMap { neighbourhood(index + 1, center, movedCoordinates(it)) }
    }

    fun points() = points.values.toList()
}

private fun addPoints(space: Space, input: List<String>) {
    input.forEachIndexed { y, line -> line.forEachIndexed { x, state -> space.add(listOf(x, y), state == '#') } }
    space.points().forEach { space.neighbours(it) }
}

private fun simulate(dimensions: Int, input: List<String>) {
    val space = Space(dimensions)
    addPoints(space, input)
    repeat(6) {
        val mutations = mutableListOf<() -> Unit>()
        space.points().forEach { point ->
            val neighbours = space.neighbours(point)
            val activeNeighboursCount = neighbours.count { it.active }
            if (point.active && (activeNeighboursCount in 2 .. 3).not())
                mutations += { point.active = false }
            if (point.active.not() && activeNeighboursCount == 3)
                mutations += { point.active = true }
        }
        mutations.forEach { it.invoke() }
    }
    val active = space.points().count { it.active }
    println(active)
}

fun main() {
    val input = loadInput(17)
    simulate(3, input)
    simulate(4, input)
}

