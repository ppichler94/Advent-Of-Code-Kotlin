package year2022

import lib.*
import lib.math.Vector
import lib.math.plus

fun main() {
    Day(12, 2022, Part12('S', "31"), Part12('a', "29")).run()
}


open class Part12(private val startChar: Char, private val example: String) : Part() {
    private lateinit var hillsMap: List<String>
    private lateinit var startPositions: List<Vector>
    private lateinit var endPosition: Vector
    private lateinit var limits: List<IntRange>

    operator fun List<String>.get(pos: Vector) = this[pos.y][pos.x]

    override fun parse(text: String) {
        hillsMap = text.split("\n")
        startPositions = hillsMap.flatMapIndexed { y, line ->
            line.mapIndexedNotNull { x, c ->
                if (c == 'E') {
                    endPosition = Vector.at(x, y)
                }
                if (c == startChar) {
                    Vector.at(x, y)
                } else
                    null
            }
        }
        limits = listOf(hillsMap.indices, hillsMap[0].indices)
    }

    override fun compute(): String {
        val traversal = TraversalBreadthFirstSearch(::neighbours)
            .startFrom(startPositions)
            .goTo(endPosition)
        return traversal.getPath().size.toString()
    }

    private fun neighbours(node: Vector, t: TraversalBreadthFirstSearch<Vector>): List<Vector> {
        return listOf(Vector.at(-1, 0), Vector.at(1, 0), Vector.at(0, -1), Vector.at(0, 1))
            .map { it + node }
            .filter { it within listOf(limits[1], limits[0]) }
            .filter { elevationOfChar(hillsMap[it]) <= elevationOfChar(hillsMap[node.y][node.x]) + 1 }
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
