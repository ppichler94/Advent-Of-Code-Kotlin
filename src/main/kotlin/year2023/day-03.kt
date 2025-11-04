package year2023

import lib.Grid2d
import lib.Position
import lib.aoc.Day
import lib.aoc.Part
import lib.math.Vector
import lib.splitLines

fun main() {
    Day(3, 2023, PartA3(), PartB3()).run()
}

open class PartA3 : Part() {
    data class Number(
        val position: Position,
        val value: Int,
        val neighbors: Set<Position>,
    ) {
        companion object {
            fun at(
                position: Position,
                grid: Grid2d<Char>,
            ): Number {
                val horizontalMoves = listOf(Vector.at(-1, 0), Vector.at(1, 0), Vector.at(0, 0))
                val positions =
                    position
                        .neighbours(horizontalMoves, grid.limits)
                        .filter { grid[it] in '0'..'9' }
                        .toMutableSet()
                while (true) {
                    val newDigitsFound =
                        positions.addAll(
                            positions.flatMap {
                                it
                                    .neighbours(horizontalMoves, grid.limits)
                                    .filter { grid[it] in '0'..'9' }
                                    .filter { it !in positions }
                            },
                        )
                    if (!newDigitsFound) {
                        break
                    }
                }
                val number =
                    positions
                        .sortedBy { it.position.x }
                        .map { grid[it] }
                        .joinToString("")
                val neighbors =
                    number.indices
                        .map { position + Vector.at(it, 0) }
                        .flatMap { it.neighbours(Position.moves(diagonals = true), grid.limits) }
                        .toSet()
                return Number(positions.minByOrNull { it.position.x }!!, number.toInt(), neighbors)
            }
        }
    }

    protected lateinit var grid: Grid2d<Char>
    protected lateinit var numbers: List<Number>

    override fun parse(text: String) {
        val lines = text.splitLines()
        grid = Grid2d.ofLines(lines)
        numbers =
            grid
                .findAll("0123456789".asIterable())
                .map { Number.at(it, grid) }
                .distinctBy { it.position }
    }

    override fun compute(): String =
        numbers
            .filter { it.neighbors.any(::isSymbol) }
            .sumOf { it.value }
            .toString()

    private fun isSymbol(position: Position) = grid[position] !in "0123456789."

    override val exampleAnswer: String
        get() = "4361"
}

class PartB3 : PartA3() {
    override fun compute(): String =
        numbers
            .asSequence()
            .flatMap { number -> number.neighbors.filter { grid[it] == '*' }.map { it to number } }
            .groupBy({ it.first }, { it.second })
            .filter { it.value.size == 2 }
            .map { it.value.first().value * it.value.last().value }
            .sum()
            .toString()

    override val exampleAnswer: String
        get() = "467835"
}