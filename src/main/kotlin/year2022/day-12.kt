package year2022

import lib.Day
import lib.Part
import lib.Search
import lib.math.Vector2i

fun main() {
    Day(12, 2022, Part12('S', "31"), Part12('a', "29")).run()
}


open class Part12(private val startChar: Char, private val example: String) : Part() {
    private lateinit var hillsMap: List<String>
    private lateinit var startPositions: List<Vector2i>
    private lateinit var endPosition: Vector2i
    private lateinit var limits: List<IntRange>

    override fun parse(text: String) {
        hillsMap = text.split("\n")
        startPositions = hillsMap.flatMapIndexed { y, line ->
            line.mapIndexedNotNull { x, c ->
                if (c == 'E') {
                    endPosition = Vector2i(x, y)
                }
                if (c == startChar) {
                    Vector2i(x, y)
                } else
                    null
            }
        }
        limits = listOf(hillsMap.indices, hillsMap[0].indices)
    }

    override fun compute(): String {
        val result = Search.bfs(::neighbours, startPositions, endPosition)
        return result.getPath(endPosition).size.toString()
    }

    private fun neighbours(node: Vector2i): List<Vector2i> {
        return listOf(Vector2i(-1, 0), Vector2i(1, 0), Vector2i(0, -1), Vector2i(0, 1))
            .map { it + node }
            .filter { it.y in limits[0] && it.x in limits[1] }
            .filter { elevationOfChar(hillsMap[it.y][it.x]) <= elevationOfChar(hillsMap[node.y][node.x]) + 1 }
    }

    private fun elevationOfChar(c: Char) = when(c) {
        in 'a' .. 'z' -> c.code - 'a'.code
        'S' -> 0
        'E' -> 25
        else -> throw IllegalArgumentException("invalid char $c")
    }

    override val exampleAnswer: String
        get() = example
}
