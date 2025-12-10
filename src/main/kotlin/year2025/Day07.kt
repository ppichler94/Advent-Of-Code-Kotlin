package year2025

import lib.Grid2d
import lib.Position
import lib.aoc.Day
import lib.aoc.Part
import lib.splitLines

fun main() {
    Day(7, 2025, PartA7(), PartB7()).run()
}

open class PartA7 : Part() {
    protected lateinit var map: Grid2d<Char>
    protected lateinit var start: Position

    override fun parse(text: String) {
        map = Grid2d.ofLines(text.splitLines())
        start = map.findAll { it == 'S' }.first()
    }

    override fun compute(): String {
        val (_, splits) = simulateManifold()
        return splits.toString()
    }

    protected fun simulateManifold(): Pair<LongArray, Long> {
        var splits = 0L
        val rays = LongArray(map.size[0])
        var currentRow = 1
        rays[start.position.x] = 1
        while (currentRow < map.size[1] - 1) {
            for (col in 0 until map.size[0]) {
                if (map[Position.at(col, currentRow)] == '^' && rays[col] > 0) {
                    rays[col - 1] += rays[col]
                    rays[col + 1] += rays[col]
                    rays[col] = 0
                    splits++
                }
            }

            currentRow++
        }
        return rays to splits
    }

    override val exampleAnswer = "21"
}

class PartB7 : PartA7() {
    override fun compute(): String {
        val (rays, _) = simulateManifold()
        return rays.sum().toString()
    }

    override val exampleAnswer = "40"
}