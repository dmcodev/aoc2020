package day02

import util.loadInput

private val pattern = Regex("^(\\d+)-(\\d+)\\s+(\\w):\\s+(.+)$")

fun main() {
    val input = loadInput(2)
    println(
        input.count {
            val match = requireNotNull(pattern.find(it))
            val (_, min, max, char, password) = match.groupValues
            password.count { it == char[0] }
                .takeIf { it >= min.toInt() && it <= max.toInt() } != null
        }
    )
    println(
        input.count {
            val match = requireNotNull(pattern.find(it))
            val (_, first, second, char, password) = match.groupValues
            sequenceOf(password[first.toInt() - 1], password[second.toInt() - 1])
                .count { it == char[0] } == 1
        }
    )
}