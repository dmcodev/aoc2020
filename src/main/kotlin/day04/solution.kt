package day04

import util.loadInput

private const val PROPERTY_VALUE_MARK = ':'

class Passport(
    attributes: List<String>
) {
    private val properties = attributes.associateBy(
        { it.substringBefore(PROPERTY_VALUE_MARK).trim() }
    ) { it.substringAfter(PROPERTY_VALUE_MARK).trim() }

    fun containsProperties(propertyNames: List<String>): Boolean {
        val validations = propertyNames.map { it to { _: String -> true } }
        return containsValidProperties(validations)
    }

    fun containsValidProperties(validations: List<Pair<String, (String) -> Boolean>>): Boolean =
        validations.all { properties[it.first]?.takeIf(it.second) != null }
}

private fun parsePassports(input: List<String>): Sequence<Passport> =
    sequence {
        val attributes = mutableListOf<String>()
        for (line in input) {
            if (line.isBlank() && attributes.isNotEmpty()) {
                yield(Passport(attributes))
                attributes.clear()
            } else {
                attributes.addAll(line.split(Regex("\\s+")))
            }
        }
        if (attributes.isNotEmpty()) yield(Passport(attributes))
    }

private val propertyNames: List<String> = listOf("byr", "iyr", "eyr", "hgt", "hcl", "ecl", "pid")

private val validations: List<Pair<String, (String) -> Boolean>> = listOf(
    "byr" to { it.length == 4 && it.toInt() in 1920 .. 2002 },
    "iyr" to { it.length == 4 && it.toInt() in 2010 .. 2020 },
    "eyr" to { it.length == 4 && it.toInt() in 2020 .. 2030 },
    "hgt" to { height -> Regex("(\\d+)(in|cm)").find(height)?.groups
        ?.let { it[1]!!.value to it[2]!!.value }
        ?.let { if (it.second == "cm") it.first.toInt() in 150 .. 193 else it.first.toInt() in 59 .. 76 }
        ?: false
    },
    "hcl" to { Regex("#[a-f0-9]{6}").matches(it) },
    "ecl" to { Regex("amb|blu|brn|gry|grn|hzl|oth").matches(it) },
    "pid" to { Regex("[0-9]{9}").matches(it) }
)

fun main() {
    var propertiesExistenceCount = 0
    var propertiesValidationCount = 0
    parsePassports(loadInput(4))
        .forEach {
            if (it.containsProperties(propertyNames)) propertiesExistenceCount++
            if (it.containsValidProperties(validations)) propertiesValidationCount++
        }
    println(propertiesExistenceCount)
    println(propertiesValidationCount)
}