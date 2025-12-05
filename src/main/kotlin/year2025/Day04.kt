package year2025

import lib.Grid2d
import lib.MutableGrid2d
import lib.Position
import lib.aoc.Day
import lib.aoc.Part
import lib.splitLines

fun main() {
    Day(4, 2025, PartA4(), PartB4()).run()
}

open class PartA4 : Part() {
    protected lateinit var grid: MutableGrid2d<Char>

    override fun parse(text: String) {
        grid = Grid2d.ofLines(text.splitLines()).toMutable()
    }

    override fun compute(): String {
        val moves = Position.moves(diagonals = true)
        return grid
            .findAll(listOf('@'))
            .count { pos -> pos.neighbours(moves, grid.limits).count { grid[it] == '@' } < 4 }
            .toString()
    }

    override val exampleAnswer = "13"
}

class PartB4 : PartA4() {
    override fun compute(): String {
        val moves = Position.moves(diagonals = true)
        var totalRemoved = 0
        do {
            val removed =
                grid
                    .findAll(listOf('@'))
                    .filter { pos -> pos.neighbours(moves, grid.limits).count { grid[it] == '@' } < 4 }
            totalRemoved += removed.count()
            removed.forEach { grid[it] = '.' }
        } while (removed.isNotEmpty())
        return totalRemoved.toString()
    }

    override val exampleAnswer = "43"
}