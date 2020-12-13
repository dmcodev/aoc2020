package day13

import util.loadInput

fun main() {
    partOne()
    partTwo()
}

fun partOne() {
    val input = loadInput(13)
    val timestamp = input[0].toLong()
    val buses = input[1].split(',').filter { it != "x" }.map { it.toLong() }
    val firstBus = buses.map { it to timestamp.nextDepartRelative(it) }
        .minByOrNull { it.second }!!
    println(firstBus.first * firstBus.second)
}

fun partTwo() {
    val input = loadInput(13)[1].split(',').asSequence()
        .withIndex().filter { it.value != "x" }.map { IndexedValue(it.index, it.value.toLong()) }
        .toList()
    var timestamp = 0L
    var period = input[0].value
    for (bus in input.asSequence().drop(1).toList()) {
        val requiredRelativeDiff = bus.index.toLong()
        var relativeDiff = timestamp.nextDepartRelative(bus.value)
        while ((requiredRelativeDiff - relativeDiff) % bus.value != 0L) {
            timestamp += period
            relativeDiff = timestamp.nextDepartRelative(bus.value)
        }
        period *= bus.value
    }
    println(timestamp)
}

private fun Long.nextDepartRelative(schedule: Long): Long = nextDepartAbsolute(schedule) - this

private fun Long.nextDepartAbsolute(schedule: Long): Long {
    var depart = ((this / schedule) * schedule)
    if (depart < this) depart += schedule
    return depart
}