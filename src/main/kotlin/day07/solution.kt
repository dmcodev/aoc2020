package day07

import util.loadInput

data class Bag(
    val id: String,
    val contains: MutableList<Pair<Int, Bag>> = mutableListOf(),
    val containing: MutableList<Bag> = mutableListOf()
) {
    fun findContainingTransitiveIds(): Set<String> =
        containing.asSequence()
            .flatMap { sequenceOf(it.id) + it.findContainingTransitiveIds() }
            .toSet()

    fun countContainedBags(): Int =
        contains.map { it.first * (it.second.countContainedBags() + 1) }.sum()

    override fun toString(): String = id
}

class Bags(input: List<String>) {

    private val containsRegex = Regex("(\\d+) ([\\w\\s]+?) bag")
    private val bags: MutableMap<String, Bag> = mutableMapOf()

    init {
        input.forEach { rule ->
            val (id, contains) = rule.split(" bags contain ")
            val bag = bags.computeIfAbsent(id) { Bag(it) }
            containsRegex.findAll(contains)
                .map { it.groupValues }
                .forEach { match ->
                    val nestedBag = bags.computeIfAbsent(match[2]) { Bag(it) }
                    bag.contains.add(match[1].toInt() to nestedBag)
                    nestedBag.containing.add(bag)
                }
        }
    }

    operator fun get(id: String): Bag = bags.getValue(id)
}

fun main() {
    val bags = Bags(loadInput(7))
    val shinyGoldBag = bags["shiny gold"]
    println(shinyGoldBag.findContainingTransitiveIds().size)
    println(shinyGoldBag.countContainedBags())
}