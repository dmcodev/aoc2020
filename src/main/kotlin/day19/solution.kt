package day19

import util.loadInput
import kotlin.math.min

private typealias ExpressionMap = Map<Int, Expression>

private sealed class Expression {
    abstract fun resolve(builder: StringBuilder, maxPatternLength: Int, expressionMap: ExpressionMap, patternLength: Int): Int
}

private class Token(val value: String) : Expression() {
    override fun resolve(builder: StringBuilder, maxPatternLength: Int, expressionMap: ExpressionMap, patternLength: Int): Int {
        builder.append(value)
        return patternLength + 1
    }
}

private class Group(val expressions: List<Expression>) : Expression() {
    override fun resolve(builder: StringBuilder, maxPatternLength: Int, expressionMap: ExpressionMap, patternLength: Int): Int {
        builder.append("(")
        var currentPatternLength = patternLength
        expressions.forEach {
            builder.append("(")
            currentPatternLength = it.resolve(builder, maxPatternLength, expressionMap, currentPatternLength)
            builder.append(")")
        }
        builder.append(")")
        return currentPatternLength
    }
}

private class Alternative(val expressions: List<Expression>) : Expression() {
    override fun resolve(builder: StringBuilder, maxPatternLength: Int, expressionMap: ExpressionMap, patternLength: Int): Int {
        builder.append("(")
        var minBranchPatterLength = Int.MAX_VALUE
        expressions.forEach {
            builder.append("(")
            minBranchPatterLength = min(minBranchPatterLength, it.resolve(builder, maxPatternLength, expressionMap, patternLength))
            builder.append(")")
            builder.append("|")
        }
        if (builder.last() == '|') {
            builder.deleteCharAt(builder.length - 1)
        }
        builder.append(")")
        return minBranchPatterLength
    }
}

private class Ref(val id: Int) : Expression() {
    override fun resolve(builder: StringBuilder, maxPatternLength: Int, expressionMap: ExpressionMap, patternLength: Int): Int {
        if (patternLength >= maxPatternLength) return patternLength
        return expressionMap.getValue(id).resolve(builder, maxPatternLength, expressionMap, patternLength)
    }
}

private class Rule(
    val id: Int,
    val expression: Expression
)

fun main() {
    val input = loadInput(19)
    solve(input)
    solve(input) { id, rule ->
        when (id) {
            8 -> "42 | 42 8"
            11 -> "42 31 | 42 11 31"
            else -> rule
        }
    }
}

private fun solve(
    input: List<String>,
    ruleMapper: (Int, String) -> String = { id, rule -> rule }
) {
    val expressionMap = mutableMapOf<Int, Expression>()
    val messages = input.drop(132)
    val maxMessageLength = messages.map { it.length }.maxOrNull()!!
    val rule = input.asSequence()
        .takeWhile { it.isNotBlank() }
        .map { line ->
            val id = line.substringBefore(":").toInt()
            val value = ruleMapper(id, line.substringAfter(": "))
            val expression = if (value.startsWith("\"")) {
                Token(value.substringAfter('"').substringBefore('"'))
            } else {
                val groups = value.split(" | ")
                    .map { group -> group.split(" ").map { Ref(it.toInt()) } }
                    .map { Group(it) }
                    .toList()
                if (groups.size == 1) groups.first() else Alternative(groups)
            }
            Rule(id, expression)
        }
        .onEach { expressionMap[it.id] = it.expression }
        .toList()
        .find { it.id == 0 }!!
        .let { rule -> StringBuilder().also { rule.expression.resolve(it, maxMessageLength, expressionMap, 0) }.toString() }
    val regex = Regex(rule)

    val validMessageCount = messages.count { regex.matches(it) }
    println(validMessageCount)
}