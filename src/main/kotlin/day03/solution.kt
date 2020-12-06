package day03

import util.loadInput

fun String.isTree(position: Int): Boolean =
    get(position % length) == '#'

fun List<String>.countTrees( deltaX: Int, deltaY: Int): Int {
    var x = 0
    var y = 0
    var count = 0
    while (y < size) {
        if (get(y).isTree(x)) count++
        y += deltaY
        x += deltaX
    }
    return count
}

fun main() {
    val input = loadInput(3)
    val trees = sequenceOf(1 to 1, 3 to 1, 5 to 1, 7 to 1, 1 to 2)
        .map { input.countTrees(it.first, it.second) }
        .toList()
    println(trees[1])
    println(trees.reduce(Int::times))
}