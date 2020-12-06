package day06

import util.loadInputGrouped

fun List<String>.anyoneAnswers(): Int =
    asSequence().flatMap { it.toCharArray().asSequence() }.distinct().count()

fun List<String>.everyoneAnswers(): Int =
    asSequence().map { it.toCharArray().toSet() }.reduce { a, b -> a.intersect(b) }.size

fun main() {
    val input = loadInputGrouped(6)
    println(input.map { it.anyoneAnswers() }.sum())
    println(input.map { it.everyoneAnswers() }.sum())
}