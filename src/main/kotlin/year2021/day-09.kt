package year2021

import lib.Grid2d
import lib.Position
import lib.TraversalBreadthFirstSearch
import lib.aoc.Day
import lib.aoc.Part

fun main() {
    Day(9, 2021, PartA9(), PartB9()).run()
}

open class PartA9 : Part() {
    protected lateinit var heightmap: Grid2d<Int>

    override fun parse(text: String) {
        val content = text.split("\n").map { it.map { c -> c.digitToInt() } }

        heightmap = Grid2d(content)
    }

    override fun compute(): String {
        val minima = getMinima()
        return minima.sumOf { heightmap[it] + 1 }.toString()
    }

    protected fun getMinima() = buildList {
        val limits = heightmap.limits
        val moves = Position.moves()
        for (y in limits[1]) {
            for (x in limits[0]) {
                val pos = Position.at(x, y)
                val neighborHeight = pos.neighbours(moves, limits).map { heightmap[it] }
                if (neighborHeight.all { heightmap[pos] < it }) {
                    add(pos)
                }
            }
        }
    }

    override val exampleAnswer: String
        get() = "15"
}

class PartB9 : PartA9() {
    override fun compute(): String {
        val minima = getMinima()
        val sizes = minima.map(::getBasinSize).sorted()
        return sizes.takeLast(3).reduce(Int::times).toString()
    }

    private fun getBasinSize(position: Position): Int {
        val limits = heightmap.limits
        val moves = Position.moves()

        val traversal = TraversalBreadthFirstSearch<Position> { pos, _ ->
            pos.neighbours(moves, limits).filter { heightmap[pos] < heightmap[it] && heightmap[it] < 9 }
        }

        return traversal.startFrom(position).traverseAll().visited.size
    }

    override val exampleAnswer: String
        get() = "1134"
}
