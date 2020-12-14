package day14

import util.loadInput

fun main() {
    partOne()
    partTwo()
}

fun partOne() {
    val input = loadInput(14)
    val mutations = mutableListOf<(Long) -> Long>()
    val memory = mutableMapOf<Long, Long>()
    input.forEach { command ->
        if (command.startsWith("mask")) {
            mutations.clear()
            command.substringAfter("= ").reversed().withIndex()
                .forEach { bit ->
                    if (bit.value == '0') {
                        mutations.add { it.and(Long.MAX_VALUE - 1L.shl(bit.index)) }
                    } else if (bit.value == '1') {
                        mutations.add { it.or(1L.shl(bit.index)) }
                    }
                }
        }
        if (command.startsWith("mem")) {
            val address = command.substringAfter("[").substringBefore("]").toLong()
            val value = command.substringAfter("= ").toLong()
            val maskedValue = mutations.fold(value) { current, mutation -> mutation.invoke(current) }
            memory[address] = maskedValue
        }
    }
    println(memory.values.sum())
}

fun partTwo() {
    val input = loadInput(14)
    var baseMask = 0L
    val floatingIndexes = mutableListOf<Int>()
    val memory = mutableMapOf<Long, Long>()
    input.forEach { command ->
        if (command.startsWith("mask")) {
            baseMask = 0L
            floatingIndexes.clear()
            command.substringAfter("= ").reversed().withIndex()
                .forEach { bit ->
                    if (bit.value == '1') {
                        baseMask += 1L.shl(bit.index)
                    } else if (bit.value == 'X') {
                        floatingIndexes.add(bit.index)
                    }
                }
        }
        if (command.startsWith("mem")) {
            val baseAddress = command.substringAfter("[").substringBefore("]").toLong()
            val value = command.substringAfter("= ").toLong()
            val address = baseAddress.or(baseMask)
            fun multiWrite(indexes: List<Int>, position: Int, currentAddress: Long) {
                if (position == indexes.size) {
                    memory[currentAddress] = value
                } else {
                    val index = indexes[position]
                    multiWrite(indexes, position + 1, currentAddress.or(1L.shl(index)))
                    multiWrite(indexes, position + 1, currentAddress.and(Long.MAX_VALUE - 1L.shl(index)))
                }
            }
            multiWrite(floatingIndexes, 0, address)
        }
    }
    println(memory.values.sum())
}