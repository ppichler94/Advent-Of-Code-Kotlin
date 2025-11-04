package year2024

import lib.*
import lib.aoc.Day
import lib.aoc.Part
import lib.math.Vector

fun main() {
    Day(6, 2024, PartA6(), PartB6()).run()
}

open class PartA6 : Part() {
    protected lateinit var grid: Grid2d<Char>

    protected fun Vector.rotate() =
        when (this) {
            Vector.UP -> Vector.RIGHT
            Vector.RIGHT -> Vector.DOWN
            Vector.DOWN -> Vector.LEFT
            Vector.LEFT -> Vector.UP
            else -> throw IllegalArgumentException("Invalid vector: $this")
        }

    override fun parse(text: String) {
        grid = Grid2d.ofLines(text.splitLines())
    }

    override fun compute(): String =
        findPath(grid)
            .map { it.first }
            .toSet()
            .size
            .toString()

    protected fun findPath(grid: BaseGrid2d<Char>): Set<Pair<Position, Vector>> {
        val visited = mutableSetOf<Pair<Position, Vector>>()
        val start = grid.findAll(listOf('^')).first()
        var dir = Vector.UP
        var pos = start.copy()

        while (pos.isInLimits(grid.limits)) {
            val next = pos + dir
            visited.add(pos to dir)
            if (!next.isInLimits(grid.limits)) {
                break
            }
            if (next to dir in visited) {
                return setOf()
            }
            if (grid[next] == '#') {
                dir = dir.rotate()
            } else {
                pos = next
            }
        }

        return visited
    }

    override val exampleAnswer: String
        get() = "41"
}

class PartB6 : PartA6() {
    private val obstructions = mutableSetOf<Position>()

    override fun compute(): String =
        grid
            .findAll(listOf('.'))
            .count {
                val newGrid: MutableGrid2d<Char> = grid.toMutable()
                newGrid[it] = '#'
                findPath(newGrid).isEmpty()
            }.toString()

    override val exampleAnswer: String
        get() = "6"
}