package day01

import util.loadInputLongs
import util.tuples

private const val magicValue = 2020L

fun main() {
    println(
        loadInputLongs(1)
            .tuples(2)
            .first { it[0] + it[1] == magicValue }
            .let { it[0] * it[1] }
    )
    println(
        loadInputLongs(1)
            .tuples(3)
            .first { it[0] + it[1] + it[2] == magicValue }
            .let { it[0] * it[1] * it[2] }
    )
}