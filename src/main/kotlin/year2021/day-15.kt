package year2021

import lib.Grid2d
import lib.Position
import lib.TraversalAStar
import lib.aoc.Day
import lib.aoc.Part

fun main() {
    Day(15, 2021, PartA15(), PartB15()).run()
}

open class PartA15 : Part() {
    protected lateinit var cave: Grid2d<Int>
    protected lateinit var end: Position
    protected lateinit var limits: List<Iterable<Int>>

    override fun parse(text: String) {
        cave = Grid2d(text.split("\n").map { line ->
            line.map { it.digitToInt() }
        })
        end = Position.at(cave.size[0] - 1, cave.size[1] - 1)
        limits = cave.limits
    }

    override fun compute(): String {
        val moves = Position.moves()

        val traversal = TraversalAStar({ pos, _ -> pos.neighbours(moves, limits).map { it to riskAt(it) } }, ::heuristic)
        traversal.startFrom(Position.at(0, 0)).goTo(end)

        return traversal.distance(end).toInt().toString()
    }

    private fun heuristic(pos: Position) = pos.manhattanDistance(end).toFloat()

    protected open fun riskAt(pos: Position) = cave[pos].toFloat()

    override val exampleAnswer: String
        get() = "40"
}

class PartB15 : PartA15() {
    override fun parse(text: String) {
        super.parse(text)
        val blockSizeX = cave.size[0]
        val blockSizeY = cave.size[1]
        limits = listOf(0..<blockSizeX * 5, 0..<blockSizeY * 5)
        end = Position.at(cave.size[0] * 5 - 1, cave.size[1] * 5 - 1)
    }

    override fun riskAt(pos: Position): Float {
        val blockSizeX = cave.size[0]
        val blockSizeY = cave.size[1]
        val blockX = pos[0] / blockSizeX
        val blockY = pos[1] / blockSizeY
        val x = pos[0] % blockSizeX
        val y = pos[1] % blockSizeY
        return wrap(cave[Position.at(x, y)] + blockX + blockY).toFloat()
    }

    private fun wrap(x: Int): Int = (x - 1).mod(9) + 1

    override val exampleAnswer: String
        get() = "315"
}
