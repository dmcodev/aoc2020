package day16

import util.loadInput

data class Rule(
    val name: String,
    val ranges: List<IntRange>
) {
    fun isValid(number: Int) = ranges.any { it.contains(number) }
}

private fun parseRule(line: String) =
    Rule(
        line.substringBefore(':'),
        line.substringAfter(": ").split(" or ").asSequence()
            .map { range -> range.split('-').let { it[0].toInt() .. it[1].toInt() } }
            .toList()
    )

private fun parseTicket(line: String) = line.split(',').map { it.toInt() }

private fun findColumnRules(candidateIndex: Int, candidates: List<Pair<Int, Set<Rule>>>, assignments: Map<Int, Rule>): Map<Int, Rule>? {
    if (candidateIndex == candidates.size) return assignments
    val columnCandidates = candidates[candidateIndex].second - assignments.values
    val columnIndex = candidates[candidateIndex].first
    return columnCandidates.mapNotNull { findColumnRules(candidateIndex + 1, candidates, assignments + (columnIndex to it)) }
        .firstOrNull { it.size == candidates.size }
}

fun main() {
    val input = loadInput(16)
    val rules = input.asSequence().takeWhile { it.isNotBlank() }.map(::parseRule).toList()
    val tickets = input.asSequence().drop(25).map(::parseTicket).toList()
    val invalidTicketCount = tickets.map { ticket -> ticket.filter { number -> rules.all { !it.isValid(number) } }.sum() }.sum()
    val validTickets = tickets.filter { ticket -> ticket.all { number -> rules.any { it.isValid(number) } } }
    val columns = validTickets[0].indices.map { i -> validTickets.map { it[i] } }
    val columnCandidateRules = columns.map { positionValues -> rules.filter { rule -> positionValues.all { rule.isValid(it) } }.toSet() }
        .withIndex().map { it.index to it.value }.sortedBy { it.second.size }
    val columnRules = requireNotNull(findColumnRules(0, columnCandidateRules, emptyMap()))
    val myTicket = parseTicket(input[22])
    val myTicketErrorRate = columnRules.filter { it.value.name.startsWith("departure") }.map { myTicket[it.key] }.fold(1L, Long::times)
    println(invalidTicketCount)
    println(myTicketErrorRate)
}