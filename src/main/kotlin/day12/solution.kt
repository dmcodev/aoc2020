package day12

import util.loadInput
import kotlin.math.absoluteValue

data class Vector(
    var x: Long,
    var y: Long
) {
    fun rotateRight(times: Int) = repeat(times) { set(y, -x) }
    fun rotateLeft(times: Int) = repeat(times) { set(-y, x) }
    fun addX(delta: Long) { x += delta }
    fun addY(delta: Long) { y += delta }
    fun scale(factor: Long) = set(x * factor, y * factor)
    fun add(other: Vector) = set(x + other.x, y + other.y)
    private fun set(nx: Long, ny: Long) { x = nx;y = ny }
}

class Instruction(line: String) {
    val type = line.first()
    val value = line.substring(1).toLong()
}

fun main() {
    val instructions = loadInput(12).map { Instruction(it) }
    partOne(instructions)
    partTwo(instructions)
}

fun partOne(instructions: List<Instruction>) {
    val position = Vector(0L, 0L)
    val direction = Vector(1L, 0L)
    move(instructions, position, direction, position)
    println(position.x.absoluteValue + position.y.absoluteValue)
}


fun partTwo(instructions: List<Instruction>) {
    val position = Vector(0L, 0L)
    val direction = Vector(10L, 1L)
    move(instructions, position, direction, direction)
    println(position.x.absoluteValue + position.y.absoluteValue)
}

fun move(
    instructions: List<Instruction>,
    position: Vector,
    direction: Vector,
    movingVector: Vector
) {
    instructions.forEach {
        when (it.type) {
            'N' -> movingVector.addY(it.value)
            'S' -> movingVector.addY(-it.value)
            'E' -> movingVector.addX(it.value)
            'W' -> movingVector.addX(-it.value)
            'L' -> direction.rotateLeft(it.value.toInt() / 90)
            'R' -> direction.rotateRight(it.value.toInt() / 90)
            'F' -> position.add(direction.copy().apply { scale(it.value) })
        }
    }
}