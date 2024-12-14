package year2024

import lib.Grid2d
import lib.Position
import lib.TraversalBreadthFirstSearch
import lib.aoc.Day
import lib.aoc.Part
import lib.math.Vector
import lib.splitLines

fun main() {
    Day(4, 2024, PartA4(), PartB4()).run()
}

open class PartA4 : Part() {
    private data class State(
        val pos: Position,
        val dir: Vector,
        val char: Char,
    )

    protected lateinit var grid: Grid2d<Char>

    override fun parse(text: String) {
        grid = Grid2d.ofLines(text.splitLines())
    }

    override fun compute(): String =
        TraversalBreadthFirstSearch<State> { state, _ ->
            val nextChar =
                when (state.char) {
                    'X' -> 'M'
                    'M' -> 'A'
                    'A' -> 'S'
                    else -> null
                }

            val nextPos = state.pos + state.dir
            if (nextChar != null && nextPos.isInLimits(grid.limits) && grid[nextPos] == nextChar) {
                listOf(State(nextPos, state.dir, nextChar))
            } else {
                emptyList()
            }
        }.startFrom(grid.findAll(listOf('X')).flatMap { Position.moves(diagonals = true).map { dir -> State(it, dir, 'X') } })
            .traverseAll()
            .visited
            .filter { it.char == 'S' }
            .toSet()
            .count()
            .toString()

    override val exampleAnswer: String
        get() = "18"

    override val customExampleData: String?
        get() =
            """
            MMMSXXMASM
            MSAMXMSMSA
            AMXSXMAAMM
            MSAMASMSMX
            XMASAMXAMM
            XXAMMXXAMA
            SMSMSASXSS
            SAXAMASAAA
            MAMMMXMMMM
            MXMXAXMASX
            """.trimIndent()
}

class PartB4 : PartA4() {
    override fun compute(): String =
        grid
            .findAll(listOf('A'))
            .filter { it.isInLimits(listOf(1 until (grid.limits[1].last()), 1 until (grid.limits[0].last()))) }
            .count { it.isXMas(grid) }
            .toString()

    private fun Position.isXMas(grid: Grid2d<Char>): Boolean {
        val topLeft = grid[this - Vector.at(-1, -1)]
        val topRight = grid[this - Vector.at(-1, 1)]
        val bottomLeft = grid[this - Vector.at(1, -1)]
        val bottomRight = grid[this - Vector.at(1, 1)]

        return (topLeft == 'M' && bottomRight == 'S' || topLeft == 'S' && bottomRight == 'M') &&
            (topRight == 'M' && bottomLeft == 'S' || topRight == 'S' && bottomLeft == 'M')
    }

    override val exampleAnswer: String
        get() = "9"
}
