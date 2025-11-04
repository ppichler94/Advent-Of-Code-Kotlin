package year2022

import lib.Grid2d
import lib.Position
import lib.TraversalAStar
import lib.aoc.Day
import lib.aoc.Part
import lib.math.Vector
import lib.memoize

fun main() {
    Day(24, 2022, PartA24(), PartB24()).run()
}

open class PartA24 : Part() {
    private lateinit var blizzards: (Int) -> Set<Position>
    private lateinit var valley: Grid2d<Char>
    private lateinit var moves: List<Vector>
    private lateinit var limits: List<Iterable<Int>>
    protected lateinit var target: Position

    override fun parse(text: String) {
        valley = Grid2d.ofLines(text.split("\n"))
        moves = Position.moves(zeroMove = true)
        limits = valley.limits
        target = Position.at(valley.size[0] - 2, valley.size[1] - 1)
        val blizzardIterator = blizzardIteratorOf(valley)
        blizzards = { _: Int -> blizzardIterator.next() }.memoize()
    }

    private fun blizzardIteratorOf(valley: Grid2d<Char>) = sequence {
        val limits = valley.size.map { 1..<it - 1 }
        val moves = mapOf(
            ">" to Vector.at(1, 0),
            "^" to Vector.at(0, -1),
            "<" to Vector.at(-1, 0),
            "v" to Vector.at(0, 1),
        )

        val posAndDir = moves.keys.flatMap { move ->
            valley.findAll(move.asIterable()).map { it to moves.getValue(move) }
        }
        var positions = posAndDir.map { it.first }
        val directions = posAndDir.map { it.second }
        while (true) {
            yield(positions.toSet())
            positions = (positions zip directions).map { (pos, dir) ->
                (pos + dir).wrapToLimits(limits)
            }
        }
    }.iterator()

    override fun compute(): String {
        val start = Position.at(1, 0)
        val traversal = TraversalAStar({ s, _ -> nextState(s) }, ::heuristic)
            .startFrom(Pair(start, 0))

        for ((position, _) in traversal) {
            if (position == target) {
                return (traversal.depth - 1).toString()
            }
        }

        error("should not be reached")
    }

    protected fun nextState(state: Pair<Position, Int>) = sequence {
        val (position, minute) = state
        val blizzardPositions = blizzards(minute)
        for (nextPosition in position.neighbours(moves, limits)) {
            if (valley[nextPosition] != '#' && nextPosition !in blizzardPositions) {
                yield(Pair(nextPosition, minute + 1) to 1f)
            }
        }
    }.asIterable()

    protected fun heuristic(state: Pair<Position, Int>): Float {
        return state.first.manhattanDistance(target).toFloat()
    }

    override val exampleAnswer: String
        get() = "18"

    override val customExampleData: String?
        get() = """
            #.######
            #>>.<^<#
            #.<..<<#
            #>v.><>#
            #<^v^^>#
            ######.#
        """.trimIndent()
}

class PartB24 : PartA24() {
    override fun compute(): String {
        val start = Position.at(1, 0)
        val traversal = TraversalAStar({ s, _ -> nextState(s) }, ::heuristic)
        var minute = 0
        var result = 0
        listOf(start to target, target to start, start to target).forEach { (start, partTarget) ->
            target = partTarget
            for ((position, partMinute) in traversal.startFrom(Pair(start, minute))) {
                if (position == target) {
                    minute = partMinute
                    result += traversal.depth
                    break
                }
            }

        }
        return (result - 1).toString()
    }

    override val exampleAnswer: String
        get() = "54"
}
