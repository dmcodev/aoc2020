package day20

import util.loadInput
import java.util.*

private enum class Side {
    TOP { override fun opposite(): Side = BOTTOM },
    RIGHT { override fun opposite(): Side = LEFT },
    BOTTOM { override fun opposite(): Side = TOP },
    LEFT { override fun opposite(): Side = RIGHT };
    abstract fun opposite(): Side
}

private data class Tile(val id: Long, val content: List<String>) {

    private val size = content.size

    val borders =
        listOf(
            Side.TOP to content[0],
            Side.BOTTOM to content[size - 1],
            Side.LEFT to content.map { it[0] }.joinToString(""),
            Side.RIGHT to content.map { it[size - 1] }.joinToString("")
        )
        .map { Border(this, it.second, it.first) }
        .associateBy { it.side }

    fun variants(): List<Tile> =
        listOf(
            this,
            rotate(),
            rotate().rotate(),
            rotate().rotate().rotate(),
            flip(),
            flip().rotate(),
            flip().rotate().rotate(),
            flip().rotate().rotate().rotate()
        ).distinct()

    private fun flip() = Tile(id, content.reversed())

    private fun rotate() = Tile(id, (0 until size).map { i -> content.map { it[i] }.joinToString("").reversed() })
}

private fun parseTiles() = loadInput(20).windowed(12, 12)
    .map {
        val id = it[0].substringAfter(" ").substringBefore(":").toLong()
        val content = it.subList(1, 11)
        Tile(id, content)
    }
    .flatMap { it.variants() }
    .toList()

private data class Border(val tile: Tile, val value: String, var side: Side)

private typealias Vector = Pair<Int, Int>
private fun Vector.x() = first
private fun Vector.y() = second

fun main() {
    val tiles = parseTiles()
    val tileMap = mutableMapOf<Vector, Tile>()
    val matchingBorders = tiles.asSequence()
        .flatMap { it.borders.values.asSequence() }
        .groupBy { it.value }
    val positionsToFill = LinkedList<Vector>()
    tileMap[0 to 0] = tiles.first()
    (0 to 0).neighbourTileVectors().forEach(positionsToFill::addLast)

    while (positionsToFill.isNotEmpty()) {
        val position = positionsToFill.pollFirst()
        if (tileMap.containsKey(position)) continue
        val usedIds = tileMap.values.map { it.id }.toSet()
        val candidateSets = sequenceOf(
                Side.TOP to tileMap[position.x() to position.y() + 1],
                Side.BOTTOM to tileMap[position.x() to position.y() - 1],
                Side.RIGHT to  tileMap[position.x() + 1 to position.y()],
                Side.LEFT to tileMap[position.x() - 1 to position.y()]
            )
            .map { neighbourToCheck ->
                val (side, neighbour) = neighbourToCheck
                if (neighbour != null) {
                    val neighbourBorderValue = neighbour.borders.getValue(side.opposite()).value
                    matchingBorders[neighbourBorderValue]
                        ?.filter { !usedIds.contains(it.tile.id)}
                        ?.filter { it.side == side }
                        ?.map { it.tile } ?: emptyList()
                } else null
            }
            .filterNotNull()
            .map { it.toSet() }
            .toList()
        val candidates = if (candidateSets.isNotEmpty()) candidateSets.fold(candidateSets.first(), Iterable<Tile>::intersect) else emptySet()
        if (candidates.size == 1) {
            val match = candidates.first()
            tileMap[position] = match
            position.neighbourTileVectors().forEach(positionsToFill::addLast)
        }
    }

    val xMin = tileMap.keys.minOf { it.x() }
    val xMax = tileMap.keys.maxOf { it.x() }
    val yMin = tileMap.keys.minOf { it.y() }
    val yMax = tileMap.keys.maxOf { it.y() }

    val topLeft = tileMap[xMin to yMax]!!
    val topRight = tileMap[xMax to yMax]!!
    val bottomRight = tileMap[xMax to yMin]!!
    val bottomLeft = tileMap[xMin to yMin]!!

    println(topLeft.id * topRight.id * bottomRight.id * bottomLeft.id)

    val image = mutableListOf<String>()

    for (y in (yMax downTo yMin)) {
        val lines = Array(8) { "" }
        for (x in (xMin .. xMax)) {
            val position = x to y
            val tile = tileMap[position]!!
            tile.content.subList(1, 9).map { it.substring(1, it.length - 1) }
                .forEachIndexed { i, line -> lines[i] += line }
        }
        image.addAll(lines)
    }

    val imageTile = Tile(1, image).variants()
        .find { monsterPositions(it.content).isNotEmpty() }!!

    val hashVectorsCount = imageTile.content.sumBy { line -> line.count { it == '#' } }
    val monsterVectorsCount = monsterPositions(imageTile.content).flatMap { it.monsterVectors() }.distinct().size
    println(hashVectorsCount - monsterVectorsCount)
}

private fun monsterPositions(image: List<String>) =
    image.flatMapIndexed { y, line ->
        line.mapIndexed { x, _ ->
            val vector = (x to y)
            if (vector.monsterVectors().all { image.isHash(it.x(), it.y()) } ) vector else null
        }.filterNotNull()
    }

private fun List<String>.isHash(x: Int, y: Int): Boolean {
    if (x < 0 || y < 0 || y >= size || x >= first().length) return false
    return get(y)[x] == '#'
}

private fun Vector.monsterVectors(): List<Vector> =
    listOf(
        (x() + 18) to y(),
        (x()) to (y() + 1),
        (x() + 5) to (y() + 1),
        (x() + 6) to (y() + 1),
        (x() + 11) to (y() + 1),
        (x() + 12) to (y() + 1),
        (x() + 17) to (y() + 1),
        (x() + 18) to (y() + 1),
        (x() + 19) to (y() + 1),
        (x() + 1) to (y() + 2),
        (x() + 4) to (y() + 2),
        (x() + 7) to (y() + 2),
        (x() + 10) to (y() + 2),
        (x() + 13) to (y() + 2),
        (x() + 16) to (y() + 2),
    )

private fun Vector.neighbourTileVectors(): List<Vector> =
    listOf(
        (x() + 1) to y(),
        (x() - 1) to y(),
        x() to (y() - 1),
        x() to (y() + 1)
    )