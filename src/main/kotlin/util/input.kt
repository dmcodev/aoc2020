package util

import java.nio.charset.StandardCharsets

fun loadInput(day: Int, fileName: String = "input"): List<String> =
    object {}.javaClass.getResourceAsStream("/day${day.toString().padStart(2, '0')}/$fileName")
        .use { it.bufferedReader(StandardCharsets.UTF_8).lineSequence().toList() }

fun loadInputGrouped(day: Int, fileName: String = "input"): List<List<String>> =
    loadInput(day, fileName)
        .fold(mutableListOf<MutableList<String>>(mutableListOf())) { result, line ->
            result.apply { if (line.isNotBlank()) last().add(line) else add(mutableListOf()) }
        }
        .filter { it.isNotEmpty() }

fun loadInputLongs(day: Int, fileName: String = "input"): List<Long> =
    loadInput(day, fileName).asSequence()
        .map { it.toLong() }
        .toList()