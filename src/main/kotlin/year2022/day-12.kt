package year2022

import lib.Day
import lib.Part
import lib.math.Vector2i

fun main() {
    Day(12, 2022, Part12('S', "31"), Part12('a', "29")).run()
}


open class Part12(private val startChar: Char, private val example: String) : Part() {
    data class Node(val pos: Vector2i, val parent: Node?) {
        val depth: Int by lazy {
            (parent?.depth ?: 0) + 1
        }
    }

    private lateinit var hillsMap: List<String>
    private lateinit var startPositions: List<Vector2i>
    private lateinit var limits: List<Iterable<Int>>

    override fun parse(text: String) {
        hillsMap = text.split("\n")
        startPositions = hillsMap.flatMapIndexed { y, line ->
            line.mapIndexed { x, c ->
                if (c == startChar) {
                    Vector2i(x, y)
                } else
                    null
            }
        }.filterNotNull()

        limits = listOf(hillsMap.indices, hillsMap[0].indices)
    }

    private fun elevationOfChar(c: Char) = when(c) {
        in 'a' .. 'z' -> c.code - 'a'.code
        'S' -> 0
        'E' -> 25
        else -> throw IllegalArgumentException("invalid char $c")
    }

    override fun compute(): String {
        return bfs(startPositions).toString()
    }

    private fun bfs(start: List<Vector2i>): Int {
        val queue = start.map { Node(it, null) }.toMutableList()
        val visited = start.toMutableSet()
        while (queue.isNotEmpty()) {
            val current = queue.removeFirst()
            val children = nextPositions(current.pos).filter { !visited.contains(it) }.toList()
            if (children.any { hillsMap[it.y][it.x] == 'E' } ) {
                return current.depth
            }
            visited.addAll(children)
            queue.addAll(children.map { Node(it, current) })
        }
        return Int.MAX_VALUE
    }

    private fun nextPositions(position: Vector2i): List<Vector2i> {
        return listOf(Vector2i(-1, 0), Vector2i(1, 0), Vector2i(0, -1), Vector2i(0, 1))
            .map { it + position }
            .filter { it.y in limits[0] && it.x in limits[1] }
            .filter { elevationOfChar(hillsMap[it.y][it.x]) <= elevationOfChar(hillsMap[position.y][position.x]) + 1 }
    }

    override val exampleAnswer: String
        get() = example
}
