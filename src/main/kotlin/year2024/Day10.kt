package year2024

import lib.Grid2d
import lib.Position
import lib.TraversalBreadthFirstSearch
import lib.aoc.Day
import lib.aoc.Part

fun main() {
    Day(10, 2024, PartA10(), PartB10()).run()
}

open class PartA10 : Part() {
    protected lateinit var map: Grid2d<Int>
    protected lateinit var limits: List<Iterable<Int>>

    override fun parse(text: String) {
        map = Grid2d(text.split("\n").map { line -> line.map { if (it == '.') -1 else it.digitToInt() } })
        limits = map.limits
    }

    override fun compute(): String {
        val moves = Position.moves(2, false, false)

        return map
            .findAll(listOf(0))
            .sumOf { start ->
                TraversalBreadthFirstSearch<Position> { pos, t ->
                    pos
                        .neighbours(moves, limits)
                        .filter { it !in t.visited }
                        .filter { map[it] == map[pos] + 1 }
                }.startFrom(start)
                    .traverseAll()
                    .visited
                    .count { map[it] == 9 }
            }.toString()
    }

    override val exampleAnswer = "36"

    override val customExampleData =
        """
        89010123
        78121874
        87430965
        96549874
        45678903
        32019012
        01329801
        10456732
        """.trimIndent()
}

class PartB10 : PartA10() {
    override fun compute(): String {
        val moves = Position.moves(2, false, false)

        return map
            .findAll(listOf(0))
            .sumOf { start ->
                TraversalBreadthFirstSearch<Pair<Position, List<Position>>> { (pos, path), _ ->
                    pos
                        .neighbours(moves, limits)
                        .filter { it !in path }
                        .filter { map[it] == map[pos] + 1 }
                        .map { Pair(it, path + it) }
                }.startFrom(start to listOf(start))
                    .traverseAll()
                    .visited
                    .count { map[it.first] == 9 }
            }.toString()
    }

    override val exampleAnswer = "81"
}