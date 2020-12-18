package day18

import util.loadInput

fun main() {
    val input = loadInput(18)
    compute(input) { Group() }
    compute(input) { ComplexGroup() }
}

private typealias Operand = (Long, Long) -> Long

private val PLUS: Operand = Long::plus

private sealed class Expression {
    abstract fun compute(): Long
}

private class Number(val value: Long) : Expression() {
    override fun compute(): Long = value
}

private open class Group(
    val expressions: MutableList<Expression> = mutableListOf(),
    val operands: MutableList<Operand> = mutableListOf()
) : Expression() {
    override fun compute(): Long {
        var result = expressions.first().compute()
        expressions.asSequence().drop(1)
            .forEachIndexed { index, expression -> result = operands[index](result, expression.compute()) }
        return result
    }
}

private class ComplexGroup(
    expressions: MutableList<Expression> = mutableListOf(),
    operands: MutableList<Operand> = mutableListOf()
) : Group(expressions, operands) {
    override fun compute(): Long {
        while (operands.contains(PLUS)) {
            val plusIndex = operands.indexOfFirst { it == PLUS }
            val sum = expressions[plusIndex].compute() + expressions[plusIndex + 1].compute()
            expressions[plusIndex] = Number(sum)
            expressions.removeAt(plusIndex + 1)
            operands.removeAt(plusIndex)
        }
        return super.compute()
    }
}

private fun compute(input: List<String>, groupFactory: () -> Group) {
    val result = input.map { line ->
        val groups = mutableListOf<Group>()
        groups.add(groupFactory())
        line.forEach { token ->
            when (token) {
                '(' -> run {
                    val group = groupFactory()
                    groups.last().expressions.add(group)
                    groups.add(group)
                }
                ')' -> groups.removeLast()
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' -> run {
                    val number = Number(token.toString().toLong())
                    groups.last().expressions.add(number)
                }
                '+' -> groups.last().operands.add(PLUS)
                '*' -> groups.last().operands.add(Long::times)
                ' ' -> Unit
            }
        }
        groups.first().compute()
    }.sum()
    println(result)
}